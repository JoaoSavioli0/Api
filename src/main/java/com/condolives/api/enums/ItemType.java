package com.condolives.api.enums;

public enum ItemType {
    PRODUTO("Produto"),
    SERVICO("Serviço");

    private final String descricao;

    ItemType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case PRODUTO -> "product";
            case SERVICO -> "service";
        };
    }

    public static ItemType fromDbValue(String value) {
        return switch (value.toLowerCase()) {
            case "product" -> PRODUTO;
            case "service" -> SERVICO;
            default -> throw new IllegalArgumentException("Tipo de item desconhecido: " + value);
        };
    }
}
