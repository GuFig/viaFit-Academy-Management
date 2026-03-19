package br.com.academy.management.model.entity;

import br.com.academy.management.model.enums.ClassificacaoImc;
import br.com.academy.management.model.enums.TipoPlano;
import br.com.academy.management.validation.ResponsavelObrigatorioParaMenorIdade;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "aluno")
@ResponsavelObrigatorioParaMenorIdade
public class Aluno implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do aluno.")
    @Size(max = 120, message = "O nome do aluno deve ter no máximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe o telefone celular.")
    @Size(max = 20, message = "O telefone celular deve ter no máximo 20 caracteres.")
    @Column(name = "fone_celular", nullable = false, length = 20)
    private String foneCelular;

    @NotBlank(message = "Informe o e-mail.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres.")
    @Column(nullable = false, length = 120, unique = true)
    private String email;

    @NotNull(message = "Informe a data de nascimento.")
    @PastOrPresent(message = "A data de nascimento nao pode estar no futuro.")
    @Column(name = "data_nascimento", nullable = false)
    private LocalDate dataNascimento;

    @NotBlank(message = "Informe o CEP.")
    @Size(max = 9, message = "O CEP deve ter no máximo 9 caracteres.")
    @Column(nullable = false, length = 9)
    private String cep;

    @NotBlank(message = "Informe o logradouro.")
    @Size(max = 160, message = "O logradouro deve ter no máximo 160 caracteres.")
    @Column(nullable = false, length = 160)
    private String logradouro;

    @NotNull(message = "Informe o peso.")
    @DecimalMin(value = "20.00", message = "O peso deve ser maior ou igual a 20 kg.")
    @DecimalMax(value = "400.00", message = "O peso deve ser menor ou igual a 400 kg.")
    @Digits(integer = 3, fraction = 2, message = "O peso deve conter até 3 dígitos inteiros e 2 casas decimais.")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal peso;

    @NotNull(message = "Informe a altura.")
    @DecimalMin(value = "0.50", message = "A altura deve ser maior ou igual a 0,50 m.")
    @DecimalMax(value = "2.50", message = "A altura deve ser menor ou igual a 2,50 m.")
    @Digits(integer = 1, fraction = 2, message = "A altura deve conter até 1 dígito inteiro e 2 casas decimais.")
    @Column(nullable = false, precision = 3, scale = 2)
    private BigDecimal altura;

    @NotNull(message = "Informe a frequência semanal.")
    @Min(value = 1, message = "A frequência semanal mínima é 1.")
    @Max(value = 7, message = "A frequência semanal máxima é 7.")
    @Column(name = "frequencia_semanal", nullable = false)
    private Integer frequenciaSemanal;

    @Column(precision = 5, scale = 2)
    private BigDecimal imc;

    @Enumerated(EnumType.STRING)
    @Column(name = "classificacao_imc", length = 20)
    private ClassificacaoImc classificacaoImc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoPlano plano;

    @Column(name = "valor_mensalidade", nullable = false, precision = 8, scale = 2)
    private BigDecimal valorMensalidade;

    @Valid
    @OneToOne(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ResponsavelLegal responsavelLegal;

    @Valid
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FichaTreino> fichasTreino = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getFoneCelular() {
        return foneCelular;
    }

    public void setFoneCelular(String foneCelular) {
        this.foneCelular = foneCelular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public BigDecimal getPeso() {
        return peso;
    }

    public void setPeso(BigDecimal peso) {
        this.peso = peso;
    }

    public BigDecimal getAltura() {
        return altura;
    }

    public void setAltura(BigDecimal altura) {
        this.altura = altura;
    }

    public Integer getFrequenciaSemanal() {
        return frequenciaSemanal;
    }

    public void setFrequenciaSemanal(Integer frequenciaSemanal) {
        this.frequenciaSemanal = frequenciaSemanal;
    }

    public BigDecimal getImc() {
        return imc;
    }

    public void setImc(BigDecimal imc) {
        this.imc = imc;
    }

    public ClassificacaoImc getClassificacaoImc() {
        return classificacaoImc;
    }

    public void setClassificacaoImc(ClassificacaoImc classificacaoImc) {
        this.classificacaoImc = classificacaoImc;
    }

    public TipoPlano getPlano() {
        return plano;
    }

    public void setPlano(TipoPlano plano) {
        this.plano = plano;
    }

    public BigDecimal getValorMensalidade() {
        return valorMensalidade;
    }

    public void setValorMensalidade(BigDecimal valorMensalidade) {
        this.valorMensalidade = valorMensalidade;
    }

    public ResponsavelLegal getResponsavelLegal() {
        return responsavelLegal;
    }

    public void setResponsavelLegal(ResponsavelLegal responsavelLegal) {
        if (this.responsavelLegal != null && this.responsavelLegal != responsavelLegal) {
            this.responsavelLegal.setAluno(null);
        }

        this.responsavelLegal = responsavelLegal;

        if (responsavelLegal != null) {
            responsavelLegal.setAluno(this);
        }
    }

    public List<FichaTreino> getFichasTreino() {
        return fichasTreino;
    }

    public void setFichasTreino(List<FichaTreino> fichasTreino) {
        this.fichasTreino = fichasTreino != null ? fichasTreino : new ArrayList<>();
        this.fichasTreino.forEach(fichaTreino -> fichaTreino.setAluno(this));
    }

    public void adicionarFichaTreino(FichaTreino fichaTreino) {
        if (fichaTreino == null) {
            return;
        }

        fichaTreino.setAluno(this);
        this.fichasTreino.add(fichaTreino);
    }

    public void removerFichaTreino(FichaTreino fichaTreino) {
        if (fichaTreino == null) {
            return;
        }

        this.fichasTreino.remove(fichaTreino);
        fichaTreino.setAluno(null);
    }
}
