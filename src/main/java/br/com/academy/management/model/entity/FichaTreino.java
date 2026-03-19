package br.com.academy.management.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "ficha_treino")
public class FichaTreino implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o exercicio.")
    @Size(max = 120, message = "O nome do exercicio deve ter no maximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String exercicio;

    @NotNull(message = "Informe as repeticoes.")
    @Min(value = 1, message = "As repeticoes devem ser no minimo 1.")
    @Max(value = 999, message = "As repeticoes devem ser no maximo 999.")
    @Column(nullable = false)
    private Integer repeticoes;

    @DecimalMin(value = "0.00", message = "A carga deve ser maior ou igual a zero.")
    @DecimalMax(value = "1000.00", message = "A carga deve ser menor ou igual a 1000 kg.")
    @Digits(integer = 4, fraction = 2, message = "A carga deve conter ate 4 digitos inteiros e 2 casas decimais.")
    @Column(precision = 6, scale = 2)
    private BigDecimal carga;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExercicio() {
        return exercicio;
    }

    public void setExercicio(String exercicio) {
        this.exercicio = exercicio;
    }

    public Integer getRepeticoes() {
        return repeticoes;
    }

    public void setRepeticoes(Integer repeticoes) {
        this.repeticoes = repeticoes;
    }

    public BigDecimal getCarga() {
        return carga;
    }

    public void setCarga(BigDecimal carga) {
        this.carga = carga;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }
}
