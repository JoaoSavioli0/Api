package com.condolives.api.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.condolives.api.entity.User.CondoMember;
import com.condolives.api.repository.User.CondoMemberRepository;
import com.condolives.api.service.TokenRevocationService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CondoMemberRepository condoMemberRepository;
    private final TokenRevocationService tokenRevocationService;

    // @Lazy nas dependências JPA evita conflito de ordem de inicialização:
    // SecurityConfig é processado antes da infraestrutura JPA estar pronta.
    public JwtAuthenticationFilter(
            JwtTokenProvider jwtTokenProvider,
            @Lazy CondoMemberRepository condoMemberRepository,
            @Lazy TokenRevocationService tokenRevocationService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.condoMemberRepository = condoMemberRepository;
        this.tokenRevocationService = tokenRevocationService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Claims claims = jwtTokenProvider.parseToken(token);

            UUID jti = UUID.fromString(claims.getId());
            if (tokenRevocationService.isRevoked(jti)) {
                filterChain.doFilter(request, response);
                return;
            }

            UUID memberId = UUID.fromString(claims.getSubject());
            CondoMember member = condoMemberRepository.findById(memberId).orElse(null);

            if (member != null && member.getActive()) {
                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(),
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_" + claims.get("role", String.class))));

                // DatabaseConfig.RlsAwareDataSource lê "condominiumId" deste Map
                // para executar: SET LOCAL app.current_condominium_id = '<uuid>'
                auth.setDetails(Map.of("condominiumId", member.getCondominiumId().toString()));

                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}
