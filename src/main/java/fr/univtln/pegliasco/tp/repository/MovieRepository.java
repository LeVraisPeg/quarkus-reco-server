package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Movie;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class MovieRepository {
    @PersistenceContext
    EntityManager entityManager;
    public List<Movie> findAll() {
        return entityManager.createQuery("SELECT m FROM Movie m", Movie.class).getResultList();
    }

    //findById
    public Movie findById(Long id) {
        return entityManager.find(Movie.class, id);
    }
    public void save(Movie movie) {
        entityManager.persist(movie);
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
}
