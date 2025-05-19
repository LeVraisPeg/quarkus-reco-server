package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.model.Movie;

import fr.univtln.pegliasco.tp.model.view.RatingId;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieRepository {
    @PersistenceContext
    EntityManager entityManager;

    public List<Movie> findAll() {
        return entityManager.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    // findById
    public Movie findById(Long id) {
        return entityManager.find(Movie.class, id);
    }

    // findPaginated
    public List<Movie> findPaginated(int page, int size) {
        return entityManager.createQuery("SELECT m FROM Movie m", Movie.class)
                .setFirstResult(page * size)
                .setMaxResults(size)
                .getResultList();
    }

    public List<Movie> findByIds(List<Long> ids) {
        return entityManager.createQuery("SELECT m FROM Movie m WHERE m.id IN :ids", Movie.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public void save(Movie movie) {
        if (movie.getId() == null) {
            // Nouvelle entité, on persiste
            entityManager.persist(movie);
        } else {
            movie = entityManager.merge(movie);
        }

        if (movie.getGenders() != null) {
            List<Gender> managedGenders = new ArrayList<>();
            for (Gender gender : movie.getGenders()) {
                if (gender.getId() != null) {
                    gender = entityManager.find(Gender.class, gender.getId());
                } else {
                    entityManager.persist(gender);
                }
                managedGenders.add(gender);
            }
            movie.setGenders(managedGenders);
        }
    }

    public List<Movie> findByTitleContainsIgnoreCase(String title) {
        return entityManager.createQuery(
                "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE :title", Movie.class)
                .setParameter("title", "%" + title.toLowerCase() + "%")
                .getResultList();
    }

    public void merge(Movie movie) {
        entityManager.merge(movie);
    }

    public void update(Movie movie) {
        entityManager.merge(movie);
    }

    public void delete(Long id) {
        Movie movie = findById(id);
        if (movie != null) {
            entityManager.remove(movie);
        }
    }

    public List<Movie> findByTitle(String title) {
        return entityManager.createQuery("SELECT m FROM Movie m WHERE m.title = :title", Movie.class)
                .setParameter("title", title)
                .getResultList();
    }

    public List<Movie> findByYear(int year) {
        return entityManager.createQuery("SELECT m FROM Movie m WHERE m.year = :year", Movie.class)
                .setParameter("year", year)
                .getResultList();
    }

    // Récupérer les films par tag
    public List<Movie> findByTag(String tag) {
        return entityManager.createQuery("SELECT m FROM Movie m JOIN m.tags t WHERE t.name = :tag", Movie.class)
                .setParameter("tag", tag)
                .getResultList();
    }

    // Récupérer les films par genre
    public List<Movie> findByGender(String gender) {
        return entityManager.createQuery("SELECT m FROM Movie m JOIN m.genders g WHERE g.name = :gender", Movie.class)
                .setParameter("gender", gender)
                .getResultList();
    }

    public void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // Vérifier si un film existe par son ID
    public boolean existsById(Long id) {
        return entityManager.createQuery("SELECT COUNT(m) FROM Movie m WHERE m.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult() > 0;
    }

    // persister un film
    public void persist(Movie movie) {
        entityManager.persist(movie);
    }

    public Map<Long, Movie> findAllAsMap() {
        return entityManager.createQuery("SELECT m FROM Movie m", Movie.class)
                .getResultStream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));
    }

}
