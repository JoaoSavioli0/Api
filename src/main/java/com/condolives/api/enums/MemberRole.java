package com.condolives.api.enums;

public enum MemberRole {
    RESIDENT("Morador"),
    GATEKEEPER("Porteiro"),
    ADMIN("Administrador"),
    COUNCIL("Conselheiro"),
    SYNDIC("Síndico"),
    STAFF("Funcionário");

    private final String descricao;

    MemberRole(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case RESIDENT -> "resident";
            case GATEKEEPER -> "gatekeeper";
            case ADMIN -> "admin";
            case COUNCIL -> "council";
            case SYNDIC -> "syndic";
            case STAFF -> "staff";
        };
    }

    public static MemberRole fromDbValue(String value) {
        return switch (value) {
            case "resident" -> RESIDENT;
            case "gatekeeper" -> GATEKEEPER;
            case "admin" -> ADMIN;
            case "council" -> COUNCIL;
            case "syndic" -> SYNDIC;
            case "staff" -> STAFF;
            default -> throw new IllegalArgumentException("Role desconhecido: " + value);
        };
    }
}
