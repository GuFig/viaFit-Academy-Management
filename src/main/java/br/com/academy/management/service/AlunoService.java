package br.com.academy.management.service;

import br.com.academy.management.dao.AlunoDAO;
import br.com.academy.management.model.entity.Aluno;
import br.com.academy.management.model.entity.FichaTreino;
import br.com.academy.management.model.entity.ResponsavelLegal;
import br.com.academy.management.model.enums.ClassificacaoImc;
import br.com.academy.management.model.enums.TipoPlano;
import br.com.academy.management.service.dto.CepResponseDTO;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class AlunoService {

    private static final BigDecimal VALOR_BASE_MENSALIDADE = new BigDecimal("100.00");

    @Inject
    private AlunoDAO alunoDAO;

    @Inject
    private CepService cepService;

    public List<Aluno> listarTodos() {
        return alunoDAO.listarTodos();
    }

    public List<ClassificacaoImc> listarClassificacoesImc() {
        List<ClassificacaoImc> classificacoes = new ArrayList<>();
        for (ClassificacaoImc classificacao : ClassificacaoImc.values()) {
            classificacoes.add(classificacao);
        }
        return classificacoes;
    }

    public Aluno novoAluno() {
        Aluno aluno = new Aluno();
        aluno.setPlano(TipoPlano.BASE);
        aluno.setValorMensalidade(VALOR_BASE_MENSALIDADE);
        aluno.setFichasTreino(new ArrayList<>());
        return aluno;
    }

    public Aluno buscarParaEdicao(Long id) {
        if (id == null) {
            return novoAluno();
        }

        Aluno aluno = alunoDAO.buscarPorIdComDetalhes(id);
        if (aluno == null) {
            throw new NegocioException("Aluno não encontrado.");
        }

        if (aluno.getFichasTreino() == null) {
            aluno.setFichasTreino(new ArrayList<>());
        }

        recalcularIndicadores(aluno);
        return aluno;
    }

    public Aluno salvar(Aluno aluno) {
        if (aluno == null) {
            throw new NegocioException("Aluno inválido para persistência.");
        }

        recalcularIndicadores(aluno);
        sincronizarRelacionamentos(aluno);

        if (aluno.getId() == null) {
            alunoDAO.persistir(aluno);
            return aluno;
        }

        return alunoDAO.atualizar(aluno);
    }

    public void excluir(Long id) {
        Aluno aluno = alunoDAO.buscarPorId(id);
        if (aluno == null) {
            throw new NegocioException("Aluno não encontrado para exclusão.");
        }
        alunoDAO.remover(aluno);
    }

    public void recalcularIndicadores(Aluno aluno) {
        if (aluno == null) {
            return;
        }

        if (aluno.getFichasTreino() == null) {
            aluno.setFichasTreino(new ArrayList<>());
        }

        if (!ehMenorDeIdade(aluno.getDataNascimento())) {
            aluno.setResponsavelLegal(null);
        }

        BigDecimal imc = calcularImc(aluno.getPeso(), aluno.getAltura());
        aluno.setImc(imc);
        aluno.setClassificacaoImc(ClassificacaoImc.fromImc(imc));

        TipoPlano planoCalculado = definirPlano(calcularIdade(aluno.getDataNascimento()), aluno.getFrequenciaSemanal());
        aluno.setPlano(planoCalculado);
        aluno.setValorMensalidade(planoCalculado.calcularMensalidade(VALOR_BASE_MENSALIDADE));

        sincronizarRelacionamentos(aluno);
    }

    public boolean ehMenorDeIdade(LocalDate dataNascimento) {
        Integer idade = calcularIdade(dataNascimento);
        return idade != null && idade < 18;
    }

    public Integer calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return null;
        }
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public BigDecimal calcularImc(BigDecimal peso, BigDecimal altura) {
        if (peso == null || altura == null || BigDecimal.ZERO.compareTo(altura) == 0) {
            return null;
        }

        return peso.divide(altura.multiply(altura), 2, RoundingMode.HALF_UP);
    }

    public TipoPlano definirPlano(Integer idade, Integer frequenciaSemanal) {
        if (idade == null || frequenciaSemanal == null) {
            return TipoPlano.BASE;
        }

        if (idade > 60 && frequenciaSemanal == 3) {
            return TipoPlano.MELHOR_IDADE;
        }
        if (idade < 18 && frequenciaSemanal == 3) {
            return TipoPlano.JUNIOR;
        }
        if (idade >= 18 && frequenciaSemanal == 5) {
            return TipoPlano.PREMIUM;
        }
        return TipoPlano.BASE;
    }

    public void adicionarFichaTreino(Aluno aluno) {
        if (aluno == null) {
            return;
        }
        aluno.adicionarFichaTreino(new FichaTreino());
    }

    public void substituirFichasTreino(Aluno aluno, List<FichaTreino> fichasTreino) {
        if (aluno == null) {
            return;
        }

        aluno.setFichasTreino(new ArrayList<>());
        if (fichasTreino == null) {
            return;
        }

        fichasTreino.forEach(aluno::adicionarFichaTreino);
    }

    public void removerFichaTreino(Aluno aluno, FichaTreino fichaTreino) {
        if (aluno == null || fichaTreino == null) {
            return;
        }
        aluno.removerFichaTreino(fichaTreino);
    }

    public void removerFichaTreino(Aluno aluno, int indice) {
        if (aluno == null || aluno.getFichasTreino() == null) {
            return;
        }
        if (indice < 0 || indice >= aluno.getFichasTreino().size()) {
            return;
        }

        aluno.removerFichaTreino(aluno.getFichasTreino().get(indice));
    }

    public boolean preencherEnderecoPorCep(Aluno aluno) {
        if (aluno == null) {
            return false;
        }

        String cepNormalizado = normalizarCep(aluno.getCep());
        if (cepNormalizado.isEmpty()) {
            return false;
        }

        if (cepNormalizado.length() < 8) {
            return false;
        }

        CepResponseDTO cepResponseDTO = cepService.consultarPorCep(cepNormalizado);
        aluno.setCep(cepResponseDTO.getCep());
        aluno.setLogradouro(montarLogradouro(cepResponseDTO));
        return true;
    }

    public void garantirResponsavelParaMenor(Aluno aluno) {
        if (aluno == null) {
            return;
        }

        if (ehMenorDeIdade(aluno.getDataNascimento()) && aluno.getResponsavelLegal() == null) {
            aluno.setResponsavelLegal(new ResponsavelLegal());
        }
    }

    private void sincronizarRelacionamentos(Aluno aluno) {
        if (aluno.getResponsavelLegal() != null) {
            aluno.getResponsavelLegal().setAluno(aluno);
        }

        List<FichaTreino> fichasTreino = aluno.getFichasTreino();
        if (fichasTreino == null) {
            aluno.setFichasTreino(new ArrayList<>());
            return;
        }

        fichasTreino.forEach(fichaTreino -> fichaTreino.setAluno(aluno));
    }

    private String normalizarCep(String cep) {
        if (cep == null) {
            return "";
        }
        return cep.replaceAll("\\D", "");
    }

    private String montarLogradouro(CepResponseDTO cepResponseDTO) {
        if (temTexto(cepResponseDTO.getLogradouro())) {
            return cepResponseDTO.getLogradouro();
        }

        StringBuilder enderecoAlternativo = new StringBuilder();
        adicionarParte(enderecoAlternativo, cepResponseDTO.getBairro(), " - ");
        adicionarParte(enderecoAlternativo, cepResponseDTO.getLocalidade(), " - ");

        if (temTexto(cepResponseDTO.getUf())) {
            if (enderecoAlternativo.length() > 0) {
                enderecoAlternativo.append('/');
            }
            enderecoAlternativo.append(cepResponseDTO.getUf());
        }

        return enderecoAlternativo.toString();
    }

    private void adicionarParte(StringBuilder builder, String valor, String separador) {
        if (!temTexto(valor)) {
            return;
        }

        if (builder.length() > 0) {
            builder.append(separador);
        }
        builder.append(valor);
    }

    private boolean temTexto(String valor) {
        return valor != null && !valor.trim().isEmpty();
    }
}
