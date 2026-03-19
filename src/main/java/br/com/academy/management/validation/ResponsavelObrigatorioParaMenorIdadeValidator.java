package br.com.academy.management.validation;

import br.com.academy.management.model.entity.Aluno;
import br.com.academy.management.model.entity.ResponsavelLegal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class ResponsavelObrigatorioParaMenorIdadeValidator implements ConstraintValidator<ResponsavelObrigatorioParaMenorIdade, Aluno> {

    @Override
    public boolean isValid(Aluno aluno, ConstraintValidatorContext context) {
        if (aluno == null || aluno.getDataNascimento() == null || !ehMenorDeIdade(aluno.getDataNascimento())) {
            return true;
        }

        boolean valido = true;
        ResponsavelLegal responsavelLegal = aluno.getResponsavelLegal();

        context.disableDefaultConstraintViolation();

        if (responsavelLegal == null || estaVazio(responsavelLegal.getNome())) {
            context.buildConstraintViolationWithTemplate("Informe o nome do responsável legal.")
                    .addPropertyNode("responsavelLegal")
                    .addPropertyNode("nome")
                    .addConstraintViolation();
            valido = false;
        }

        if (responsavelLegal == null || estaVazio(responsavelLegal.getParentesco())) {
            context.buildConstraintViolationWithTemplate("Informe o parentesco do responsável legal.")
                    .addPropertyNode("responsavelLegal")
                    .addPropertyNode("parentesco")
                    .addConstraintViolation();
            valido = false;
        }

        return valido;
    }

    private boolean ehMenorDeIdade(LocalDate dataNascimento) {
        return Period.between(dataNascimento, LocalDate.now()).getYears() < 18;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
