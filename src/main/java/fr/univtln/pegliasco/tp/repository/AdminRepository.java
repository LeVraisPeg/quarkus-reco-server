package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Admin;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
@ApplicationScoped
public class AdminRepository {
    @PersistenceContext
    EntityManager entityManager;

    public List<Admin> findAll() {
        return entityManager.createQuery("SELECT a FROM Admin a WHERE a.role = :role", Admin.class)
                .setParameter("role", fr.univtln.pegliasco.tp.model.Account.Role.ADMIN)
                .getResultList();
    }


}
