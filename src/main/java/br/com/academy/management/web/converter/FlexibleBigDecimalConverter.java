package br.com.academy.management.web.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@FacesConverter(value = "flexibleBigDecimalConverter", managed = true)
public class FlexibleBigDecimalConverter implements Converter<BigDecimal> {

    private static final Locale PT_BR = new Locale("pt", "BR");

    @Override
    public BigDecimal getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(normalizar(value));
        } catch (NumberFormatException exception) {
            throw new ConverterException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Valor numérico inválido.",
                    "Use somente números, vírgula ou ponto."));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, BigDecimal value) {
        if (value == null) {
            return "";
        }

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(PT_BR);
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00", symbols);
        decimalFormat.setParseBigDecimal(true);
        return decimalFormat.format(value);
    }

    private String normalizar(String valor) {
        String normalizado = valor.trim().replace(" ", "");

        if (normalizado.contains(",") && normalizado.contains(".")) {
            if (normalizado.lastIndexOf(',') > normalizado.lastIndexOf('.')) {
                normalizado = normalizado.replace(".", "").replace(",", ".");
            } else {
                normalizado = normalizado.replace(",", "");
            }
        } else if (normalizado.contains(",")) {
            normalizado = normalizado.replace(".", "").replace(",", ".");
        }

        return normalizado;
    }
}
