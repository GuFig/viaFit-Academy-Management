package br.com.academy.management.web.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Comparator;

public final class FacesMessageUtil {

    private FacesMessageUtil() {
    }

    public static void addInfo(String resumo, String detalhe) {
        addMessage(FacesMessage.SEVERITY_INFO, resumo, detalhe);
    }

    public static void addError(String resumo, String detalhe) {
        addMessage(FacesMessage.SEVERITY_ERROR, resumo, detalhe);
    }

    public static void addValidationMessages(ConstraintViolationException exception) {
        exception.getConstraintViolations().stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(ConstraintViolation::getMessage)
                .distinct()
                .forEach(message -> addError("Validação", message));
    }

    private static void addMessage(FacesMessage.Severity severity, String resumo, String detalhe) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, resumo, detalhe));
    }
}
