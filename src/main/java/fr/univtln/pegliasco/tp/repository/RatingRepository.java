package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;
import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.model.RatingCache;
import fr.univtln.pegliasco.tp.model.view.RatingId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class RatingRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Rating> findPaginated(int page, int size) {
        return entityManager.createQuery("SELECT r FROM Rating r", Rating.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }


    public Rating findById(Long id) {
        return entityManager.find(Rating.class, id);
    }

    //findCacheByAccountId
    public List<RatingCache> findCacheByAccountId(Long accountId) {
        String query = "SELECT r FROM RatingCache r WHERE r.account.id = :accountId";
        return entityManager.createQuery(query, RatingCache.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    // findByAccountIdAndMovieId
    public List<Rating> findByAccountIdAndMovieId(Long accountId, Long movieId) {
        String query = "SELECT r FROM Rating r WHERE r.account.id = :accountId AND r.movie.id = :movieId";
        return entityManager.createQuery(query, Rating.class)
                .setParameter("accountId", accountId)
                .setParameter("movieId", movieId)
                .getResultList();
    }

    public void update(Rating rating) {
        entityManager.merge(rating);
    }



    public void add(Rating rating) {
        entityManager.persist(rating);
    }


    public void delete(Long id) {
        Rating rating = findById(id);
        if (rating != null) {
            entityManager.remove(rating);
        }
    }

    // findByAccountId
    public List<Rating> findByAccountId(Long accountId) {
        List<Rating> ratings = entityManager
                .createQuery("SELECT r FROM Rating r WHERE r.account.id = :accountId", Rating.class)
                .setParameter("accountId", accountId)
                .getResultList();
        return MakeNoise.applyLapplaceNoise(ratings);
    }

    public List<RatingId> findAllId() {
        return entityManager.createQuery(
                "SELECT new RatingId(r.account.id, r.movie.id,r.rate) FROM Rating r",
                RatingId.class).getResultList();
    }

    public boolean ratingExistsForUser(Long id) {
        return entityManager.createQuery("SELECT COUNT(r) FROM Rating r WHERE r.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    public double getAverageRating(Long movieId) {
        Double avg = entityManager
                .createQuery("SELECT AVG(r.rate) FROM Rating r WHERE r.movie.id = :movieId", Double.class)
                .setParameter("movieId", movieId)
                .getSingleResult();
        return avg != null ? avg : 0.0;
    }



}
