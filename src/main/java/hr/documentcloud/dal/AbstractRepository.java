package hr.documentcloud.dal;

import lombok.extern.log4j.Log4j2;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
@Log4j2
public abstract class AbstractRepository<T> {

    @PersistenceContext
    protected EntityManager em;

    public void persist(T entity) {
        log.info("Persisting {}.", entity);
        em.persist(entity);
        log.info("Persisted.");
    }

    public T merge(T entity) {
        log.info("Updating {}.", entity);
        T merged = em.merge(entity);
        log.info("Updated.");
        return merged;
    }

}
