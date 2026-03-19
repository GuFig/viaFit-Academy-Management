package br.com.academy.management.web.bean;

import br.com.academy.management.model.entity.Aluno;
import br.com.academy.management.model.entity.FichaTreino;
import br.com.academy.management.service.AlunoService;
import br.com.academy.management.service.NegocioException;
import br.com.academy.management.web.util.FacesMessageUtil;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.faces.application.FacesMessage;
import javax.ejb.EJBException;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ConstraintViolationException;
import java.io.Serializable;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Named
@ViewScoped
public class AlunoCadastroBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final Integer FREQUENCIA_TRES_DIAS = 3;
    private static final Integer FREQUENCIA_CINCO_DIAS = 5;
    private static final Integer FREQUENCIA_OUTROS_DIAS = 7;

    @Inject
    private AlunoService alunoService;

    private Long id;
    private Aluno aluno;
    private boolean menorDeIdade;
    private String treinosState;

    public void carregar() {
        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        aluno = alunoService.buscarParaEdicao(id);
        atualizarRegras();
    }

    public void atualizarRegras() {
        if (aluno == null) {
            aluno = alunoService.novoAluno();
        }

        normalizarFrequenciaSemanal();
        alunoService.recalcularIndicadores(aluno);
        menorDeIdade = alunoService.ehMenorDeIdade(aluno.getDataNascimento());
        alunoService.garantirResponsavelParaMenor(aluno);
    }

    public void adicionarTreino() {
        sincronizarTreinosDigitados();
        alunoService.adicionarFichaTreino(aluno);
    }

    public void buscarEnderecoPorCepAutomaticamente() {
        consultarCep(true);
    }

    public String removerTreino(int indice) {
        sincronizarTreinosDigitados();
        alunoService.removerFichaTreino(aluno, indice);
        return null;
    }

    public String salvar() {
        sincronizarEstadoTela();
        if (!validarCamposObrigatorios()) {
            FacesContext.getCurrentInstance().validationFailed();
            return null;
        }

        try {
            alunoService.salvar(aluno);
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.getFlash().setKeepMessages(true);
            FacesMessageUtil.addInfo("Aluno salvo", "Cadastro gravado com sucesso.");
            return "/alunos.xhtml?faces-redirect=true";
        } catch (ConstraintViolationException exception) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesMessageUtil.addValidationMessages(exception);
            return null;
        } catch (EJBException exception) {
            Throwable causa = exception.getCausedByException();
            if (causa instanceof ConstraintViolationException) {
                FacesContext.getCurrentInstance().validationFailed();
                FacesMessageUtil.addValidationMessages((ConstraintViolationException) causa);
                return null;
            }

            FacesContext.getCurrentInstance().validationFailed();
            FacesMessageUtil.addError("Falha ao salvar", "Não foi possível gravar o cadastro do aluno.");
            return null;
        } catch (NegocioException exception) {
            FacesContext.getCurrentInstance().validationFailed();
            FacesMessageUtil.addError("Falha ao salvar", exception.getMessage());
            return null;
        }
    }

    public String getTituloPagina() {
        return id == null ? "Novo aluno" : "Editar aluno";
    }

    public String getSubtituloPagina() {
        return id == null
                ? "Cadastre dados pessoais, indicadores e a ficha de treino em um único fluxo."
                : "Atualize as informações do aluno e mantenha os cálculos sincronizados automaticamente.";
    }

    public Integer getIdade() {
        return alunoService.calcularIdade(aluno != null ? aluno.getDataNascimento() : null);
    }

    public String getIdadeDescricao() {
        Integer idade = getIdade();
        return idade != null ? idade + " anos" : "Idade pendente";
    }

    public LocalDate getDataMaximaNascimento() {
        return LocalDate.now();
    }

    public String getClassificacaoDescricao() {
        return aluno != null && aluno.getClassificacaoImc() != null
                ? aluno.getClassificacaoImc().getDescricao()
                : "Aguardando dados";
    }

    public String getPlanoDescricao() {
        return aluno != null && aluno.getPlano() != null
                ? aluno.getPlano().getDescricao()
                : "Plano Base";
    }

    public List<SelectItem> getOpcoesFrequenciaSemanal() {
        List<SelectItem> opcoes = new ArrayList<>();
        opcoes.add(new SelectItem(FREQUENCIA_TRES_DIAS, "3 dias"));
        opcoes.add(new SelectItem(FREQUENCIA_CINCO_DIAS, "5 dias"));
        opcoes.add(new SelectItem(FREQUENCIA_OUTROS_DIAS, "Outros dias"));
        return opcoes;
    }

    public boolean isEdicao() {
        return id != null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Aluno getAluno() {
        return aluno;
    }

    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    public boolean isMenorDeIdade() {
        return menorDeIdade;
    }

    public String getTreinosState() {
        return treinosState;
    }

    public void setTreinosState(String treinosState) {
        this.treinosState = treinosState;
    }

    private void sincronizarEstadoTela() {
        normalizarFrequenciaSemanal();
        menorDeIdade = alunoService.ehMenorDeIdade(aluno != null ? aluno.getDataNascimento() : null);
        alunoService.garantirResponsavelParaMenor(aluno);
    }

    private void normalizarFrequenciaSemanal() {
        Integer frequenciaAtual = aluno != null ? aluno.getFrequenciaSemanal() : null;
        if (frequenciaAtual != null
                && !FREQUENCIA_TRES_DIAS.equals(frequenciaAtual)
                && !FREQUENCIA_CINCO_DIAS.equals(frequenciaAtual)) {
            aluno.setFrequenciaSemanal(FREQUENCIA_OUTROS_DIAS);
        }
    }

    private void sincronizarTreinosDigitados() {
        if (aluno == null) {
            aluno = alunoService.novoAluno();
        }

        if (treinosState == null || treinosState.trim().isEmpty()) {
            return;
        }

        alunoService.substituirFichasTreino(aluno, converterTreinosState());
    }

    private boolean validarCamposObrigatorios() {
        boolean valido = true;

        valido = validarTextoObrigatorio(aluno != null ? aluno.getNome() : null, "Informe o nome do aluno.") && valido;
        valido = validarTextoObrigatorio(aluno != null ? aluno.getFoneCelular() : null, "Informe o telefone celular.") && valido;
        valido = validarTextoObrigatorio(aluno != null ? aluno.getEmail() : null, "Informe o e-mail.") && valido;
        valido = validarDataObrigatoria(aluno != null ? aluno.getDataNascimento() : null, "Informe a data de nascimento.") && valido;
        valido = validarTextoObrigatorio(aluno != null ? aluno.getCep() : null, "Informe o CEP.") && valido;
        valido = validarTextoObrigatorio(aluno != null ? aluno.getLogradouro() : null, "Informe o logradouro.") && valido;
        valido = validarNumeroObrigatorio(aluno != null ? aluno.getPeso() : null, "Informe o peso.") && valido;
        valido = validarNumeroObrigatorio(aluno != null ? aluno.getAltura() : null, "Informe a altura.") && valido;
        valido = validarNumeroObrigatorio(aluno != null ? aluno.getFrequenciaSemanal() : null, "Informe a frequência semanal.") && valido;

        if (menorDeIdade) {
            valido = validarTextoObrigatorio(
                    aluno != null && aluno.getResponsavelLegal() != null ? aluno.getResponsavelLegal().getNome() : null,
                    "Informe o nome do responsável legal.") && valido;
            valido = validarTextoObrigatorio(
                    aluno != null && aluno.getResponsavelLegal() != null ? aluno.getResponsavelLegal().getParentesco() : null,
                    "Informe o parentesco do responsável legal.") && valido;
        }

        return valido;
    }

    private boolean validarTextoObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.trim().isEmpty()) {
            adicionarMensagemValidacao(mensagem);
            return false;
        }
        return true;
    }

    private boolean validarDataObrigatoria(LocalDate valor, String mensagem) {
        if (valor == null) {
            adicionarMensagemValidacao(mensagem);
            return false;
        }
        if (valor.isAfter(LocalDate.now())) {
            adicionarMensagemValidacao("A data de nascimento nao pode estar no futuro.");
            return false;
        }
        return true;
    }

    private boolean validarNumeroObrigatorio(Object valor, String mensagem) {
        if (valor == null) {
            adicionarMensagemValidacao(mensagem);
            return false;
        }
        return true;
    }

    private void adicionarMensagemValidacao(String mensagem) {
        FacesContext.getCurrentInstance().addMessage(
                null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validação", mensagem));
    }

    private void consultarCep(boolean silenciosoParaCepIncompleto) {
        try {
            boolean enderecoPreenchido = alunoService.preencherEnderecoPorCep(aluno);
            if (!enderecoPreenchido && !silenciosoParaCepIncompleto && possuiCepIncompleto()) {
                FacesContext.getCurrentInstance().validationFailed();
                adicionarMensagemValidacao("Informe um CEP com 8 dígitos.");
            }
        } catch (NegocioException exception) {
            FacesContext.getCurrentInstance().validationFailed();
            adicionarMensagemValidacao(exception.getMessage());
        }
    }

    private boolean possuiCepIncompleto() {
        if (aluno == null || aluno.getCep() == null) {
            return false;
        }

        String cepNormalizado = aluno.getCep().replaceAll("\\D", "");
        return !cepNormalizado.isEmpty() && cepNormalizado.length() < 8;
    }

    private List<FichaTreino> converterTreinosState() {
        List<FichaTreino> fichasTreino = new ArrayList<>();

        try (JsonReader jsonReader = Json.createReader(new StringReader(treinosState))) {
            JsonArray jsonArray = jsonReader.readArray();
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject jsonObject = jsonArray.getJsonObject(i);
                FichaTreino fichaTreino = new FichaTreino();
                fichaTreino.setExercicio(normalizarTexto(jsonObject.getString("exercicio", "")));
                fichaTreino.setRepeticoes(converterInteiro(jsonObject.getString("repeticoes", "")));
                fichaTreino.setCarga(converterDecimal(jsonObject.getString("carga", "")));
                fichasTreino.add(fichaTreino);
            }
        } catch (RuntimeException ignored) {
            return fichasTreino;
        }

        return fichasTreino;
    }

    private String normalizarTexto(String valor) {
        if (valor == null) {
            return null;
        }

        String texto = valor.trim();
        return texto.isEmpty() ? null : texto;
    }

    private Integer converterInteiro(String valor) {
        String texto = normalizarTexto(valor);
        if (texto == null) {
            return null;
        }

        try {
            return Integer.valueOf(texto);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private BigDecimal converterDecimal(String valor) {
        String texto = normalizarTexto(valor);
        if (texto == null) {
            return null;
        }

        try {
            return new BigDecimal(normalizarDecimal(texto));
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private String normalizarDecimal(String valor) {
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
