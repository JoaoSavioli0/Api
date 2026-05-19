package com.condolives.api.enums;

public enum NotificationType {
    COMMENT("comment"),
    LIKE("like"),
    TICKET_UPDATE("ticket_update"),
    TIMELINE_UPDATE("timeline_update"),
    POLL_CLOSED("poll_closed"),
    BOOKING_UPDATE("booking_update"),
    VISITOR_ARRIVED("visitor_arrived"),
    DELIVERY("delivery"),
    NOTICE("notice"),
    GENERAL("general");

    private final String dbValue;

    NotificationType(String dbValue) {
        this.dbValue = dbValue;
    }

    public String toDbValue() {
        return dbValue;
    }

    public static NotificationType fromDbValue(String value) {
        for (NotificationType t : values()) {
            if (t.dbValue.equals(value)) return t;
        }
        throw new IllegalArgumentException("Tipo desconhecido: " + value);
    }
}
