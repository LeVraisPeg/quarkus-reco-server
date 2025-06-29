package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Gender;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
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
        entityManager.flush();
    }

    public void delete(Long id) {
        Gender gender = findById(id);
        if (gender != null) {
            entityManager.remove(gender);
        }
    }

    public Gender findByName(String name) {
        try {
            return entityManager.createQuery("SELECT g FROM Gender g WHERE g.name = :name", Gender.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }




}
