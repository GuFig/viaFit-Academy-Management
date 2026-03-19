package br.com.academy.management.web.converter;

import br.com.academy.management.model.enums.ClassificacaoImc;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "classificacaoImcConverter", managed = true)
public class ClassificacaoImcConverter implements Converter<ClassificacaoImc> {

    @Override
    public ClassificacaoImc getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            return ClassificacaoImc.valueOf(value);
        } catch (IllegalArgumentException exception) {
            throw new ConverterException(new FacesMessage(
                    FacesMessage.SEVERITY_ERROR,
                    "Classificação de IMC inválida.",
                    "Selecione uma classificação válida."));
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, ClassificacaoImc value) {
        return value != null ? value.name() : "";
    }
}
