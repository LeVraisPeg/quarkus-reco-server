package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;
import fr.univtln.pegliasco.tp.model.Rating;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class RatingRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Rating> findAll() {
        return entityManager.createQuery("SELECT r FROM Rating r", Rating.class).getResultList();
    }

    public Rating findById(Long id) {
        return entityManager.find(Rating.class, id);
    }

    // findByAccountIdAndMovieId
    public Rating findByAccountIdAndMovieId(Long accountId, Long movieId) {
        return entityManager
                .createQuery("SELECT r FROM Rating r WHERE r.account.id = :accountId AND r.movie.id = :movieId",
                        Rating.class)
                .setParameter("accountId", accountId)
                .setParameter("movieId", movieId)
                .getSingleResult();
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
}
