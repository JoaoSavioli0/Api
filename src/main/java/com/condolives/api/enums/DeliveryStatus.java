package com.condolives.api.enums;

public enum DeliveryStatus {
    PENDENTE("Aguardando retirada"),
    RETIRADO("Retirado");

    private final String descricao;

    DeliveryStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case PENDENTE -> "pending";
            case RETIRADO -> "picked_up";
        };
    }

    public static DeliveryStatus fromDbValue(String value) {
        return switch (value) {
            case "pending" -> PENDENTE;
            case "picked_up" -> RETIRADO;
            default -> throw new IllegalArgumentException("Status desconhecido: " + value);
        };
    }
}
