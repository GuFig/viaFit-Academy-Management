package br.com.academy.management.dao;

import br.com.academy.management.model.entity.Aluno;

import javax.ejb.Stateless;
import java.util.List;

@Stateless
public class AlunoDAO extends GenericDAO<Aluno, Long> {

    @Override
    protected Class<Aluno> getEntityClass() {
        return Aluno.class;
    }

    public List<Aluno> listarTodos() {
        return listarTodosOrdenadosPor("nome");
    }

    public Aluno buscarPorIdComDetalhes(Long id) {
        List<Aluno> resultado = entityManager.createQuery(
                "SELECT DISTINCT a "
                + "FROM Aluno a "
                + "LEFT JOIN FETCH a.responsavelLegal "
                + "LEFT JOIN FETCH a.fichasTreino "
                + "WHERE a.id = :id", Aluno.class)
                .setParameter("id", id)
                .getResultList();

        return resultado.isEmpty() ? null : resultado.get(0);
    }
}
