package br.com.academy.management.web.bean;

import br.com.academy.management.model.entity.Aluno;
import br.com.academy.management.model.enums.ClassificacaoImc;
import br.com.academy.management.model.enums.TipoPlano;
import br.com.academy.management.service.AlunoService;
import br.com.academy.management.service.NegocioException;
import br.com.academy.management.web.util.FacesMessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Named
@ViewScoped
public class AlunoListaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private AlunoService alunoService;

    private List<Aluno> alunos;
    private List<ClassificacaoImc> classificacoesImc;

    @PostConstruct
    public void inicializar() {
        carregarDados();
    }

    public void carregarDados() {
        alunos = alunoService.listarTodos();
        classificacoesImc = alunoService.listarClassificacoesImc();
    }

    public String novo() {
        return "/aluno-form.xhtml?faces-redirect=true";
    }

    public String editar(Long id) {
        return "/aluno-form.xhtml?faces-redirect=true&id=" + id;
    }

    public String excluir(Long id) {
        try {
            alunoService.excluir(id);
            carregarDados();
            FacesMessageUtil.addInfo("Aluno excluído", "O cadastro do aluno foi removido com sucesso.");
        } catch (NegocioException exception) {
            FacesMessageUtil.addError("Exclusão não realizada", exception.getMessage());
        }
        return null;
    }

    public int getTotalAlunos() {
        return alunos != null ? alunos.size() : 0;
    }

    public long getTotalMenores() {
        if (alunos == null) {
            return 0L;
        }

        return alunos.stream()
                .filter(aluno -> alunoService.ehMenorDeIdade(aluno.getDataNascimento()))
                .count();
    }

    public BigDecimal getMensalidadeMedia() {
        if (alunos == null || alunos.isEmpty()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal soma = alunos.stream()
                .map(Aluno::getValorMensalidade)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return soma.divide(BigDecimal.valueOf(alunos.size()), 2, RoundingMode.HALF_UP);
    }

    public String planoStyleClass(TipoPlano plano) {
        if (plano == null) {
            return "plan-chip-base";
        }

        switch (plano) {
            case JUNIOR:
                return "plan-chip-junior";
            case PREMIUM:
                return "plan-chip-premium";
            case MELHOR_IDADE:
                return "plan-chip-melhor-idade";
            case BASE:
            default:
                return "plan-chip-base";
        }
    }

    public String planoIcon(TipoPlano plano) {
        if (plano == null) {
            return "pi pi-id-card";
        }

        switch (plano) {
            case JUNIOR:
                return "pi pi-users";
            case PREMIUM:
                return "pi pi-star";
            case MELHOR_IDADE:
                return "pi pi-heart";
            case BASE:
            default:
                return "pi pi-id-card";
        }
    }

    public List<Aluno> getAlunos() {
        return alunos;
    }

    public List<ClassificacaoImc> getClassificacoesImc() {
        return classificacoesImc;
    }
}
