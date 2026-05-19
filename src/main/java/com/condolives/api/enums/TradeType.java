package com.condolives.api.enums;

public enum TradeType {
    DOACAO("Doação"),
    TROCA("Troca"),
    VENDA("Venda"),
    SERVICO("Serviço");

    private final String descricao;

    TradeType(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public String toDbValue() {
        return switch (this) {
            case DOACAO  -> "donation";
            case TROCA   -> "trade";
            case VENDA   -> "sale";
            case SERVICO -> "service";
        };
    }

    public static TradeType fromDbValue(String value) {
        return switch (value.toLowerCase()) {
            case "donation" -> DOACAO;
            case "trade"    -> TROCA;
            case "sale"     -> VENDA;
            case "service"  -> SERVICO;
            default -> throw new IllegalArgumentException("Tipo de troca desconhecido: " + value);
        };
    }
}
