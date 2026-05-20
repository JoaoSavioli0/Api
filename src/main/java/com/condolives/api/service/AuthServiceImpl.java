package com.condolives.api.service;

import java.time.LocalDate;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.condolives.api.dto.auth.CreateAccountRequest;
import com.condolives.api.dto.auth.LoginRequest;
import com.condolives.api.dto.auth.LoginResponse;
import com.condolives.api.dto.auth.RegisterRequest;
import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.entity.User.UserAccount;
import com.condolives.api.enums.MemberRole;
import com.condolives.api.exception.ServiceException;
import com.condolives.api.repository.Condominium.CondominiumRepository;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.repository.User.UserAccountRepository;
import com.condolives.api.security.EncryptionService;
import com.condolives.api.security.JwtTokenProvider;
import com.condolives.api.security.RateLimitService;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final CondoMemberRepository memberRepository;
    private final UserAccountRepository userAccountRepository;
    private final CondominiumRepository condominiumRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RateLimitService rateLimitService;
    private final EncryptionService encryptionService;
    private final TokenRevocationService tokenRevocationService;

    @Transactional
    public void createAccount(CreateAccountRequest request) {
        if (userAccountRepository.findByEmail(request.email()).isPresent()) {
            throw new ServiceException("Email já cadastrado", 409);
        }
        if (request.cpf() != null && !request.cpf().isBlank()
                && userAccountRepository.existsByCpf(encryptionService.encryptDeterministic(request.cpf()))) {
            throw new ServiceException("CPF já cadastrado", 409);
        }
        userAccountRepository.save(UserAccount.builder()
                .name(request.name())
                .email(request.email())
                .cpf(request.cpf())
                .phone(request.phone())
                .passwordHash(passwordEncoder.encode(request.password()))
                .build());
    }

    public LoginResponse login(LoginRequest request) {
        if (rateLimitService.isAccountLocked(request.email())) {
            throw new ServiceException(
                    "Conta temporariamente bloqueada por excesso de tentativas. Tente novamente mais tarde.", 429);
        }

        CondoMember member = memberRepository
                .findFirstByUserEmail(request.email())
                .orElseThrow(() -> new ServiceException("Usuário não encontrado", 404));

        if (!member.getCondominiumId().toString().equals(request.condominiumId().toString())) {
            throw new ServiceException("Usuário não possui acesso à este condomínio", 403);
        }

        if (!member.getActive()) {
            throw new ServiceException("Conta desativada", 403);
        }

        UserAccount user = member.getUser();

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            rateLimitService.recordAccountFailure(request.email());
            throw new ServiceException("Credenciais inválidas", 401);
        }

        rateLimitService.resetAccountFailures(request.email());

        String token = jwtTokenProvider.generateToken(member.getId(), member.getRole());

        return new LoginResponse(token, "Bearer", member.getId(), user.getName(), user.getEmail(),
                member.getUnitAddress(), user.getAvatarUrl(), member.getCondominiumId());
    }

    public void logout(String token) {
        try {
            Claims claims = jwtTokenProvider.parseToken(token);
            UUID jti = UUID.fromString(claims.getId());
            tokenRevocationService.revokeToken(jti, claims.getExpiration().toInstant());
        } catch (Exception ignored) {
            // Token inválido ou já expirado — logout é idempotente
        }
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (!condominiumRepository.existsById(request.condominiumId())) {
            throw new ServiceException("Condomínio não encontrado", 404);
        }

        // Reutiliza o user_account se a pessoa já tem conta; cria se não tiver.
        UserAccount user = userAccountRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    if (request.cpf() != null
                            && userAccountRepository.existsByCpf(
                                    encryptionService.encryptDeterministic(request.cpf()))) {
                        throw new ServiceException("CPF já cadastrado", 409);
                    }
                    return userAccountRepository.save(UserAccount.builder()
                            .name(request.name())
                            .email(request.email())
                            .cpf(request.cpf())
                            .rg(request.rg())
                            .phone(request.phone())
                            .passwordHash(passwordEncoder.encode(request.password()))
                            .build());
                });

        if (memberRepository.existsByUserIdAndCondominiumId(user.getId(), request.condominiumId())) {
            throw new ServiceException("Usuário já é membro deste condomínio", 409);
        }

        // O endpoint público de registro só permite RESIDENT; outros papéis exigem
        // convite admin.
        MemberRole role = MemberRole.RESIDENT;

        CondoMember member = memberRepository.save(CondoMember.builder()
                .userId(user.getId())
                .condominiumId(request.condominiumId())
                .unitId(request.unitId())
                .guardianId(request.guardianId())
                .role(role)
                .joinedAt(LocalDate.now())
                .active(true)
                .build());

        String jwt = jwtTokenProvider.generateToken(member.getId(), member.getRole());

        return new LoginResponse(jwt, "Bearer", member.getId(), user.getName(), user.getEmail(),
                member.getUnitAddress(), user.getAvatarUrl(), member.getCondominiumId());
    }
}
