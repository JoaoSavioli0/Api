package com.condolives.api.security;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;

@Service
public class RateLimitService {

    // Per-IP: 5 tentativas/minuto (token bucket com recarga intervalar)
    private final Map<String, Bucket> ipBuckets = new ConcurrentHashMap<>();

    // Per-IP: histórico de falhas para bloqueio estendido de 15 min após 10 falhas/30 min
    private final Map<String, Deque<Instant>> ipFailureWindows = new ConcurrentHashMap<>();
    private final Map<String, Instant> ipExtendedBlocks = new ConcurrentHashMap<>();

    // Per-conta: histórico de falhas para bloqueio de conta após 10 tentativas/30 min
    private final Map<String, Deque<Instant>> accountFailureWindows = new ConcurrentHashMap<>();
    private final Map<String, Instant> accountLocks = new ConcurrentHashMap<>();

    /**
     * Tenta consumir um token para o IP informado.
     * Retorna false se o IP estiver bloqueado por rate limit.
     */
    public boolean tryConsumeIpToken(String ip) {
        Instant extBlock = ipExtendedBlocks.get(ip);
        if (extBlock != null && Instant.now().isBefore(extBlock)) {
            return false;
        }
        return ipBuckets.computeIfAbsent(ip, k -> newIpBucket()).tryConsume(1);
    }

    /** Registra uma falha de login para o IP. Bloqueia por 15 min após 10 falhas em 30 min. */
    public void recordIpFailure(String ip) {
        Deque<Instant> window = ipFailureWindows.computeIfAbsent(ip, k -> new ArrayDeque<>());
        Instant now = Instant.now();
        synchronized (window) {
            window.removeIf(t -> t.isBefore(now.minus(30, ChronoUnit.MINUTES)));
            window.addLast(now);
            if (window.size() >= 10) {
                ipExtendedBlocks.put(ip, now.plus(15, ChronoUnit.MINUTES));
                window.clear();
            }
        }
    }

    /** Verifica se a conta está bloqueada por tentativas excessivas. */
    public boolean isAccountLocked(String email) {
        Instant locked = accountLocks.get(email);
        return locked != null && Instant.now().isBefore(locked);
    }

    /**
     * Registra uma falha de credenciais para a conta.
     * Bloqueia por 30 min após 10 tentativas em 30 min.
     * (Desbloqueio via e-mail requer endpoint adicional.)
     */
    public void recordAccountFailure(String email) {
        Deque<Instant> window = accountFailureWindows.computeIfAbsent(email, k -> new ArrayDeque<>());
        Instant now = Instant.now();
        synchronized (window) {
            window.removeIf(t -> t.isBefore(now.minus(30, ChronoUnit.MINUTES)));
            window.addLast(now);
            if (window.size() >= 10) {
                accountLocks.put(email, now.plus(30, ChronoUnit.MINUTES));
                window.clear();
            }
        }
    }

    /** Limpa o histórico de falhas após login bem-sucedido. */
    public void resetAccountFailures(String email) {
        accountFailureWindows.remove(email);
        accountLocks.remove(email);
    }

    private Bucket newIpBucket() {
        // 5 tokens; recarga de todos os 5 tokens a cada 1 minuto (bloqueio implícito após esgotamento)
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                .build();
    }
}
