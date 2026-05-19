package com.condolives.api.enums;

public enum UnitType {
    APARTMENT("Apartamento"),
    HOUSE("Casa");

    private final String descricao;

    UnitType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case APARTMENT -> "apartment";
            case HOUSE     -> "house";
        };
    }

    public static UnitType fromDbValue(String value) {
        return switch (value.toLowerCase()) {
            case "apartment" -> APARTMENT;
            case "house"     -> HOUSE;
            default -> throw new IllegalArgumentException("Tipo de unidade desconhecido: " + value);
        };
    }
}
