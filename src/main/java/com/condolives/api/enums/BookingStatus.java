package com.condolives.api.enums;

public enum BookingStatus {
    PENDENTE("Pendente"),
    CONCLUIDO("Concluido"),
    CANCELADO("Cancelado"),
    REPROVADO("Reprovado");

    private final String descricao;

    BookingStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case PENDENTE -> "pending";
            case CONCLUIDO -> "completed";
            case CANCELADO -> "cancelled";
            case REPROVADO -> "rejected";
        };
    }

    public static BookingStatus fromDbValue(String value) {
        return switch (value) {
            case "pending" -> PENDENTE;
            case "completed" -> CONCLUIDO;
            case "cancelled" -> CANCELADO;
            case "rejected" -> REPROVADO;
            default -> throw new IllegalArgumentException("Status desconhecido: " + value);
        };
    }
}
