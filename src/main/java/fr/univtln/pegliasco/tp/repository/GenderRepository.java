package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Gender;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class GenderRepository {
    @PersistenceContext
    EntityManager entityManager;


    public List<Gender> findAll() {
        return entityManager.createQuery("SELECT g FROM Gender g", Gender.class).getResultList();
    }

    public Gender findById(Long id) {
        return entityManager.find(Gender.class, id);
    }

    public void add(Gender gender) {
        entityManager.persist(gender);
    }

    public void update(Gender gender) {
        entityManager.merge(gender);
    }

    public void delete(Long id) {
        Gender gender = findById(id);
        if (gender != null) {
            entityManager.remove(gender);
        }
    }

}
