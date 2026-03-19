package br.com.academy.management.model.enums;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum TipoPlano {

    BASE("Plano Base", new BigDecimal("0.00")),
    JUNIOR("Plano Junior", new BigDecimal("0.20")),
    PREMIUM("Plano Premium", new BigDecimal("0.15")),
    MELHOR_IDADE("Plano Melhor Idade", new BigDecimal("0.25"));

    private final String descricao;
    private final BigDecimal desconto;

    TipoPlano(String descricao, BigDecimal desconto) {
        this.descricao = descricao;
        this.desconto = desconto;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public BigDecimal calcularMensalidade(BigDecimal valorBase) {
        if (valorBase == null) {
            return BigDecimal.ZERO;
        }

        return valorBase.multiply(BigDecimal.ONE.subtract(desconto)).setScale(2, RoundingMode.HALF_UP);
    }
}
