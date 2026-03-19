package br.com.academy.management.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ResponsavelObrigatorioParaMenorIdadeValidator.class)
public @interface ResponsavelObrigatorioParaMenorIdade {

    String message() default "Responsável legal é obrigatório para alunos menores de idade.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
