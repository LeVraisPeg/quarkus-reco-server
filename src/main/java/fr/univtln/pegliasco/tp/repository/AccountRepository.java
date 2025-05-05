package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Account;



import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

import java.util.List;


@ApplicationScoped
public class AccountRepository {
    @PersistenceContext
    private EntityManager em;

    // Récupérer tous les comptes
    public List<Account> findAll() {
        return em.createQuery("SELECT a FROM Account a", Account.class).getResultList();
    }

    // Trouver un compte par son ID
    public Account findById(Long id) {
        return em.find(Account.class, id);
    }
    // Récupérer un compte par son nom d'utilisateur
    public Account findByNom(String nom) {
        try {
            return em.createQuery("SELECT a FROM Account a WHERE a.nom = :nom", Account.class)
                    .setParameter("nom", nom)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    // Ajouter un compte
    public void persist(Account account) {
        em.persist(account);
    }
    // Mettre à jour un compte
    public void update(Account account) {
        em.merge(account);
    }
    // Supprimer un compte
    public void delete(Account account) {
        em.remove(em.contains(account) ? account : em.merge(account));
    }
    // Trouver un compte par son nom et mot de passe
    public Account findByNomAndPassword(String nom, String password) {
        try {
            return em.createQuery("SELECT a FROM Account a WHERE a.nom = :nom AND a.password = :password", Account.class)
                    .setParameter("nom", nom)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    // Récupérer un compte par son rôle
    public List<Account> findByRole(String role) {
        return em.createQuery("SELECT a FROM Account a WHERE a.role = :role", Account.class)
                .setParameter("role", role)
                .getResultList();
    }
}
