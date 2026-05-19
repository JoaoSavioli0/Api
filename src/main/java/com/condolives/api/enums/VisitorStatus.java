package com.condolives.api.enums;

public enum VisitorStatus {
    AGUARDANDO("Aguardando"),
    CHEGOU("Chegou"),
    SAIU("Saiu"),
    CANCELADO("Cancelado");

    private final String descricao;

    VisitorStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case AGUARDANDO -> "pending";
            case CHEGOU     -> "arrived";
            case SAIU       -> "left";
            case CANCELADO  -> "cancelled";
        };
    }

    public static VisitorStatus fromDbValue(String value) {
        return switch (value) {
            case "pending"   -> AGUARDANDO;
            case "arrived"   -> CHEGOU;
            case "left"      -> SAIU;
            case "cancelled" -> CANCELADO;
            default -> throw new IllegalArgumentException("Status desconhecido: " + value);
        };
    }
}
