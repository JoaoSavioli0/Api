package com.condolives.api.enums;

public enum TradeStatus {
    ABERTO("Aberto"),
    FECHADO("Fechado");

    private final String descricao;

    TradeStatus(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case ABERTO  -> "open";
            case FECHADO -> "closed";
        };
    }

    public static TradeStatus fromDbValue(String value) {
        return switch (value.toLowerCase()) {
            case "open"   -> ABERTO;
            case "closed" -> FECHADO;
            default -> throw new IllegalArgumentException("Status de troca desconhecido: " + value);
        };
    }
}
