package br.com.academy.management.model.enums;

import java.math.BigDecimal;

public enum ClassificacaoImc {

    BAIXO_PESO("Baixo peso"),
    NORMAL("Normal"),
    SOBREPESO("Sobrepeso"),
    OBESIDADE_I("Obesidade I"),
    OBESIDADE_II("Obesidade II"),
    OBESIDADE_III("Obesidade III");

    private final String descricao;

    ClassificacaoImc(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static ClassificacaoImc fromImc(BigDecimal imc) {
        if (imc == null) {
            return null;
        }

        if (imc.compareTo(new BigDecimal("18.50")) < 0) {
            return BAIXO_PESO;
        }
        if (imc.compareTo(new BigDecimal("25.00")) < 0) {
            return NORMAL;
        }
        if (imc.compareTo(new BigDecimal("30.00")) < 0) {
            return SOBREPESO;
        }
        if (imc.compareTo(new BigDecimal("35.00")) < 0) {
            return OBESIDADE_I;
        }
        if (imc.compareTo(new BigDecimal("40.00")) < 0) {
            return OBESIDADE_II;
        }
        return OBESIDADE_III;
    }
}
