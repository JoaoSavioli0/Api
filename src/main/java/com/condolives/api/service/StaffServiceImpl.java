package com.condolives.api.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.staff.CreateStaffRequest;
import com.condolives.api.dto.staff.StaffResponse;
import com.condolives.api.dto.staff.UpdateStaffRequest;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.Staff;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.enums.StaffCategory;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.CompanyRepository;
import com.condolives.api.repository.User.StaffRepository;
import com.condolives.api.repository.User.UserAccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {

    private static final String INVITE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int INVITE_LENGTH = 8;

    private final StaffRepository staffRepository;
    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;
    private final CondoMemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<StaffResponse> list(UUID condominiumId, StaffCategory category) {
        var staff = category != null
                ? staffRepository.findAllByCondominiumIdAndCategory(condominiumId, category)
                : staffRepository.findAllByCondominiumId(condominiumId);

        return staff.stream().map(StaffResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public StaffResponse getById(UUID id, UUID condominiumId) {
        return staffRepository.findByIdAndCondominiumId(id, condominiumId)
                .map(StaffResponse::from)
                .orElseThrow(() -> new ServiceException("Funcionário não encontrado", 404));
    }

    @Transactional
    public StaffResponse create(CreateStaffRequest request, UUID condominiumId) {
        if (request.companyId() != null
                && companyRepository.findByIdAndCondominiumId(request.companyId(), condominiumId).isEmpty()) {
            throw new ServiceException("Empresa não encontrada neste condomínio", 404);
        }

        UUID userId = null;
        String inviteCode = null;

        if (Boolean.TRUE.equals(request.createAccess())) {
            if (request.email() == null || request.email().isBlank()) {
                throw new ServiceException("Email é obrigatório para criar acesso ao sistema", 400);
            }

            String code = generateInviteCode();
            inviteCode = code;

            if (request.cpf() != null && userAccountRepository.existsByCpf(request.cpf())) {
                throw new ServiceException("CPF já cadastrado no sistema", 409);
            }

            UserAccount user = userAccountRepository.findByEmail(request.email())
                    .orElseGet(() -> userAccountRepository.save(UserAccount.builder()
                            .name(request.name())
                            .email(request.email())
                            .phone(request.phone())
                            .cpf(request.cpf())
                            .rg(request.rg())
                            .passwordHash(passwordEncoder.encode(code))
                            .build()));

            if (memberRepository.existsByUserIdAndCondominiumId(user.getId(), condominiumId)) {
                throw new ServiceException("Usuário já é membro deste condomínio", 409);
            }

            memberRepository.save(CondoMember.builder()
                    .userId(user.getId())
                    .condominiumId(condominiumId)
                    .role(MemberRole.STAFF)
                    .joinedAt(LocalDate.now())
                    .active(true)
                    .build());

            userId = user.getId();
        }

        // cpf/rg ficam em user_account quando há vínculo; sem vínculo ficam no staff.
        boolean hasUser = userId != null;
        Staff staff = staffRepository.save(Staff.builder()
                .condominiumId(condominiumId)
                .userId(userId)
                .name(request.name())
                .phone(request.phone())
                .cpf(hasUser ? null : request.cpf())
                .rg(hasUser ? null : request.rg())
                .jobTitle(request.jobTitle())
                .category(request.category())
                .address(request.address())
                .companyId(request.companyId())
                .joinedAt(request.joinedAt() != null ? request.joinedAt() : LocalDate.now())
                .active(true)
                .build());

        return StaffResponse.from(
                staffRepository.findByIdAndCondominiumId(staff.getId(), condominiumId).orElseThrow(),
                inviteCode);
    }

    @Transactional
    public StaffResponse update(UUID id, UpdateStaffRequest request, UUID condominiumId) {
        Staff existing = staffRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Funcionário não encontrado", 404));

        if (request.companyId() != null
                && companyRepository.findByIdAndCondominiumId(request.companyId(), condominiumId).isEmpty()) {
            throw new ServiceException("Empresa não encontrada neste condomínio", 404);
        }

        boolean linkedToUser = existing.getUserId() != null;
        Staff updated = Staff.builder()
                .id(existing.getId())
                .condominiumId(existing.getCondominiumId())
                .userId(existing.getUserId())
                .name(request.name() != null ? request.name() : existing.getName())
                .phone(request.phone() != null ? request.phone() : existing.getPhone())
                // cpf/rg só podem ser alterados aqui quando não há user_account vinculado
                .cpf(linkedToUser ? null : (request.cpf() != null ? request.cpf() : existing.getCpf()))
                .rg(linkedToUser ? null : (request.rg() != null ? request.rg() : existing.getRg()))
                .jobTitle(request.jobTitle() != null ? request.jobTitle() : existing.getJobTitle())
                .category(request.category() != null ? request.category() : existing.getCategory())
                .address(request.address() != null ? request.address() : existing.getAddress())
                .companyId(request.companyId() != null ? request.companyId() : existing.getCompanyId())
                .joinedAt(existing.getJoinedAt())
                .active(existing.getActive())
                .build();

        staffRepository.save(updated);

        return StaffResponse.from(staffRepository.findByIdAndCondominiumId(id, condominiumId).orElseThrow());
    }

    @Transactional
    public void dismiss(UUID id, UUID condominiumId) {
        Staff staff = staffRepository.findByIdAndCondominiumId(id, condominiumId)
                .orElseThrow(() -> new ServiceException("Funcionário não encontrado", 404));

        if (!staff.getActive()) {
            throw new ServiceException("Funcionário já está inativo", 409);
        }

        staff.setActive(false);
        staffRepository.save(staff);
    }

    private String generateInviteCode() {
        SecureRandom rng = new SecureRandom();
        StringBuilder sb = new StringBuilder(INVITE_LENGTH);
        for (int i = 0; i < INVITE_LENGTH; i++) {
            sb.append(INVITE_ALPHABET.charAt(rng.nextInt(INVITE_ALPHABET.length())));
        }
        return sb.toString();
    }
}
