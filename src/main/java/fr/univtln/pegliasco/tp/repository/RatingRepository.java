package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.encryption.differential_privacy.MakeNoise;
import fr.univtln.pegliasco.tp.model.Rating;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RatingRepository {

    @PersistenceContext
    EntityManager entityManager;

    public List<Rating> findAll() {
        return entityManager.createQuery("SELECT r FROM Rating r", Rating.class).getResultList();
    }

    public List<Rating> findPaginated(int page, int size) {
        return entityManager.createQuery("SELECT r FROM Rating r", Rating.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }


    public Rating findById(Long id) {
        return entityManager.find(Rating.class, id);
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
}
