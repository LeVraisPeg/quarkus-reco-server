package fr.univtln.pegliasco.tp.repository;


import fr.univtln.pegliasco.tp.model.Account;
import fr.univtln.pegliasco.tp.model.Tag;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;

import static io.quarkus.hibernate.orm.panache.PanacheEntityBase.find;

@ApplicationScoped
public class TagRepository {
    @PersistenceContext
    EntityManager entityManager;

    // Récupérer tous les tags
    public List<Tag> findAll() {
        return entityManager.createQuery("SELECT t FROM Tag t", Tag.class).getResultList();
    }

    // Ajouter un tag
    public void add(Tag tag) {
        entityManager.persist(tag);
    }

    // Supprimer un tag par son ID
    public void delete(Long id) {
        Tag tag = findById(id);
        if (tag != null) {
            entityManager.remove(tag);
        }
    }

    // Mettre à jour un tag par son ID
    public void update(Tag tag) {
        entityManager.merge(tag);
        entityManager.flush();
    }

    // Récupérer un tag par son ID
    public Tag findById(Long id) {
        return entityManager.find(Tag.class, id);
    }



    public Optional<Tag> findByNameAndAccount(String name, Account account) {
        return entityManager.createQuery("SELECT t FROM Tag t WHERE t.name = :name AND t.account = :account", Tag.class)
                .setParameter("name", name)
                .setParameter("account", account)
                .getResultList()
                .stream()
                .findFirst();
    }

    //findByMovieId
    public List<Tag> findByMovieId(Long movieId) {
        return entityManager.createQuery("SELECT t FROM Tag t JOIN t.movies m WHERE m.id = :movieId", Tag.class)
                .setParameter("movieId", movieId)
                .getResultList();
    }

}
