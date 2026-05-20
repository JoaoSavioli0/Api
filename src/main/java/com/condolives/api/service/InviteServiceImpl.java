package com.condolives.api.service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.auth.LoginResponse;
import com.condolives.api.dto.invite.ClaimInviteRequest;
import com.condolives.api.dto.invite.CreateInviteRequest;
import com.condolives.api.dto.invite.InvitePreviewResponse;
import com.condolives.api.dto.invite.InviteResponse;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.MemberInvite;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Condominium.CondominiumRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.MemberInviteRepository;
import com.condolives.api.repository.User.UserAccountRepository;
import com.condolives.api.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {

    private static final int INVITE_VALIDITY_DAYS = 7;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final MemberInviteRepository inviteRepository;
    private final CondoMemberRepository memberRepository;
    private final UserAccountRepository userAccountRepository;
    private final CondominiumRepository condominiumRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public InviteResponse createInvite(CreateInviteRequest request, UUID condominiumId) {
        MemberRole role = parseRole(request.role());

        CondoMember member = memberRepository.save(CondoMember.builder()
                .condominiumId(condominiumId)
                .unitId(request.unitId())
                .role(role)
                .guardianId(request.guardianId())
                .joinedAt(LocalDate.now())
                .active(false)
                .build());

        String token = String.format("%05d", SECURE_RANDOM.nextInt(100000));
        Instant expiresAt = Instant.now().plus(INVITE_VALIDITY_DAYS, ChronoUnit.DAYS);

        MemberInvite invite = inviteRepository.save(MemberInvite.builder()
                .memberId(member.getId())
                .token(token)
                .expiresAt(expiresAt)
                .build());

        // Reload to populate EAGER unit relationship so getUnitAddress() works.
        CondoMember loaded = memberRepository.findById(member.getId()).orElse(member);
        return InviteResponse.from(invite, loaded);
    }

    @Transactional(readOnly = true)
    public List<InviteResponse> listInvites(UUID condominiumId) {
        return inviteRepository.findAllByMemberCondominiumIdOrderByCreatedAtDesc(condominiumId)
                .stream()
                .map(i -> InviteResponse.from(i, i.getMember()))
                .toList();
    }

    @Transactional(readOnly = true)
    public InvitePreviewResponse getPreview(String token) {
        MemberInvite invite = findValidInvite(token);

        CondoMember member = memberRepository.findById(invite.getMemberId())
                .orElseThrow(() -> new ServiceException("Convite inválido", 404));

        String condoName = condominiumRepository.findById(member.getCondominiumId())
                .map(c -> c.getName())
                .orElse("Condomínio");

        return new InvitePreviewResponse(
                invite.getId(),
                condoName,
                member.getUnitAddress(),
                member.getRole(),
                invite.getExpiresAt());
    }

    @Transactional
    public LoginResponse claimInvite(String token, ClaimInviteRequest request) {
        MemberInvite invite = findValidInvite(token);

        CondoMember member = memberRepository.findById(invite.getMemberId())
                .orElseThrow(() -> new ServiceException("Convite inválido", 404));

        UserAccount user = userAccountRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    if (request.cpf() != null && userAccountRepository.existsByCpf(request.cpf())) {
                        throw new ServiceException("CPF já cadastrado", 409);
                    }
                    return userAccountRepository.save(UserAccount.builder()
                            .name(request.name())
                            .email(request.email())
                            .cpf(request.cpf())
                            .phone(request.phone())
                            .passwordHash(passwordEncoder.encode(request.password()))
                            .build());
                });

        if (memberRepository.existsByUserIdAndCondominiumId(user.getId(), member.getCondominiumId())) {
            throw new ServiceException("Usuário já é membro deste condomínio", 409);
        }

        member.setUserId(user.getId());
        member.setActive(true);
        memberRepository.save(member);

        invite.setUsedAt(Instant.now());
        inviteRepository.save(invite);

        CondoMember reloaded = memberRepository.findById(member.getId()).orElse(member);
        String jwt = jwtTokenProvider.generateToken(reloaded.getId(), reloaded.getRole());
        return new LoginResponse(jwt, "Bearer", reloaded.getId(), user.getName(), user.getEmail(),
                reloaded.getUnitAddress(), reloaded.getUser().getAvatarUrl(), reloaded.getCondominiumId());
    }

    @Transactional
    public void revokeInvite(UUID inviteId, UUID condominiumId) {
        MemberInvite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new ServiceException("Convite não encontrado", 404));

        CondoMember member = memberRepository.findById(invite.getMemberId())
                .orElseThrow(() -> new ServiceException("Membro não encontrado", 404));

        if (!member.getCondominiumId().equals(condominiumId)) {
            throw new ServiceException("Acesso negado", 403);
        }

        if (invite.getUsedAt() != null) {
            throw new ServiceException("Convite já utilizado", 409);
        }

        inviteRepository.delete(invite);
        memberRepository.delete(member);
    }

    private MemberInvite findValidInvite(String token) {
        MemberInvite invite = inviteRepository.findByToken(token)
                .orElseThrow(() -> new ServiceException("Convite não encontrado", 404));

        if (invite.getUsedAt() != null) {
            throw new ServiceException("Convite já utilizado", 409);
        }

        if (Instant.now().isAfter(invite.getExpiresAt())) {
            throw new ServiceException("Convite expirado", 410);
        }

        return invite;
    }

    private MemberRole parseRole(String role) {
        if (role == null || role.isBlank())
            return MemberRole.RESIDENT;
        try {
            return MemberRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("Papel inválido", 422);
        }
    }
}
