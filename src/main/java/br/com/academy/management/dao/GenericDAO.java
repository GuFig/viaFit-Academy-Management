package br.com.academy.management.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

public abstract class GenericDAO<T, ID extends Serializable> implements Serializable {

    private static final long serialVersionUID = 1L;

    @PersistenceContext(unitName = "academyPU")
    protected EntityManager entityManager;

    protected abstract Class<T> getEntityClass();

    public T buscarPorId(ID id) {
        return entityManager.find(getEntityClass(), id);
    }

    public void persistir(T entidade) {
        entityManager.persist(entidade);
    }

    public T atualizar(T entidade) {
        return entityManager.merge(entidade);
    }

    public void remover(T entidade) {
        T entidadeGerenciada = entityManager.contains(entidade) ? entidade : entityManager.merge(entidade);
        entityManager.remove(entidadeGerenciada);
    }

    public List<T> listarTodosOrdenadosPor(String atributo) {
        return entityManager.createQuery(
                "SELECT e FROM " + getEntityClass().getSimpleName() + " e ORDER BY e." + atributo,
                getEntityClass())
                .getResultList();
    }
}
