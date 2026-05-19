package com.condolives.api.security;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitService rateLimitService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        if (!"/auth/login".equals(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String ip = extractClientIp(request);

        if (!rateLimitService.tryConsumeIpToken(ip)) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"erro\":\"Muitas tentativas de login. Tente novamente em alguns minutos.\",\"status\":429}");
            return;
        }

        chain.doFilter(request, response);

        // Registra falha para rastreamento de bloqueio estendido por IP
        if (response.getStatus() >= 400 && response.getStatus() < 500) {
            rateLimitService.recordIpFailure(ip);
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
