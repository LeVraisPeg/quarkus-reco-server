package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.User;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;

@ApplicationScoped
public class UserRepository {
    @PersistenceContext
    EntityManager entityManager;

    public List<User> findAll() {
        return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
    }


}
