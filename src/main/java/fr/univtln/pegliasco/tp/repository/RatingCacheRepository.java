package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.RatingCache;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class RatingCacheRepository {
    @PersistenceContext
    private EntityManager entityManager;

    public void add(RatingCache ratingCache) {
        entityManager.persist(ratingCache);
    }

    public void update(RatingCache ratingCache) {
        entityManager.merge(ratingCache);
    }

    public void delete(RatingCache ratingCache) {
        entityManager.remove(entityManager.contains(ratingCache) ? ratingCache : entityManager.merge(ratingCache));
    }

    public RatingCache findById(Long id) {
        return entityManager.find(RatingCache.class, id);
    }

    public List<RatingCache> findPaginated(int page, int size) {
        return entityManager.createQuery("SELECT r FROM RatingCache r", RatingCache.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<RatingCache> findByAccountId(Long accountId) {
        String query = "SELECT r FROM RatingCache r WHERE r.account.id = :accountId";
        return entityManager.createQuery(query, RatingCache.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }



}
