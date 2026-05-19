package com.condolives.api.enums;

public enum Importance {
    HIGH, MEDIUM, LOW;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static Importance fromDbValue(String value) {
        return switch (value.toLowerCase()) {
            case "high"   -> HIGH;
            case "medium" -> MEDIUM;
            case "low"    -> LOW;
            default -> throw new IllegalArgumentException("Importância desconhecida: " + value);
        };
    }
}
