package com.condolives.api.enums;

public enum StaffCategory {
    INTERNAL("internal"),
    OUTSOURCED("outsourced");

    private final String dbValue;

    StaffCategory(String dbValue) {
        this.dbValue = dbValue;
    }

    public String toDbValue() {
        return dbValue;
    }

    public static StaffCategory fromDbValue(String value) {
        return switch (value) {
            case "internal" -> INTERNAL;
            case "outsourced" -> OUTSOURCED;
            default -> throw new IllegalArgumentException("Categoria desconhecida: " + value);
        };
    }
}
