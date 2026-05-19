package com.condolives.api.controller.helpers;

import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;

import com.condolives.api.enums.MemberRole;

public class AuthHelper {

    public static UUID condominiumId(Authentication authentication) {
        var details = (Map<String, Object>) authentication.getDetails();
        return UUID.fromString((String) details.get("condominiumId"));
    }

    public static MemberRole memberRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> MemberRole.valueOf(a.substring(5)))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Role não encontrada no token"));
    }

    public static boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
