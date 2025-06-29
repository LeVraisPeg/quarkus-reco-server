package fr.univtln.pegliasco.tp.repository;

import fr.univtln.pegliasco.tp.model.Account;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.logging.Logger;

@ApplicationScoped
public class AccountRepository {
    @PersistenceContext
    private EntityManager em;
    Logger logger;
    private static final int SALT_LENGTH = 16;

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
        // Générer un salt et hacher le mot de passe
        String salt = generateSalt();
        String hashedPassword = hashPassword(account.getPassword(), salt);
        account.setPassword(hashedPassword + ":" + salt);
        // Enregistrer le compte dans la base de données
        em.persist(account);
    }

    // Mettre à jour un compte
    public Account update(Account account) {
        String salt = generateSalt();
        String hashedPassword = hashPassword(account.getPassword(), salt);
        account.setPassword(hashedPassword + ":" + salt);
        try {
            em.merge(account);
        } catch (Exception e) {
            // Gérer l'exception si nécessaire
            logger.info("Erreur lors de la mise à jour du compte : " + e.getMessage());
        }
        return account;
    }

    // Supprimer un compte
    public void delete(Account account) {
        em.remove(em.contains(account) ? account : em.merge(account));
    }

    private String hashPassword(String password, String salt) {
        // Ajouter le salt au mot de passe avant de le hacher
        String saltedPassword = password + salt;
        return DigestUtils.sha256Hex(saltedPassword); // Hachage avec SHA-256
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Hex.encodeHexString(salt);
    }

    private boolean checkPassword(String password, String storedPassword) {
        // Extraire le mot de passe haché et le salt à partir de la chaîne
        // "hashedPassword:salt"
        String[] parts = storedPassword.split(":");
        String hashedStoredPassword = parts[0]; // Le mot de passe haché stocké
        String salt = parts[1]; // Le salt utilisé pour hacher le mot de passe

        // Hacher le mot de passe saisi avec le même salt
        String hashedPassword = hashPassword(password, salt);

        // Comparer les deux hachages
        return hashedPassword.equals(hashedStoredPassword);
    }

    // Trouver un compte par son nom et mot de passe
    public Account findByNomAndPassword(String nom, String password) {
        try {
            // Récupérer l'enseignant par email
            // Account account = em.createQuery("SELECT a FROM Account a WHERE a.nom =
            // :nom", Account.class)
            // .setParameter("nom", nom)
            // .getSingleResult();
            Account account = findByNom(nom);
            // Si l'enseignant existe et que le mot de passe est correct
            if (account != null && checkPassword(password, account.getPassword())) {
                return account;
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    // Récupérer un compte par son rôle
    public List<Account> findByRole(String role) {
        return em.createQuery("SELECT a FROM Account a WHERE a.role = :role", Account.class)
                .setParameter("role", Account.Role.valueOf(role))
                .getResultList();
    }

    public Map<Long, Account> findAllAsMap() {
        return em.createQuery("SELECT a FROM Account a", Account.class)
                .getResultStream()
                .collect(Collectors.toMap(Account::getId, Function.identity()));
    }

    public Account findByEmail(String email) {
        try {
            return em.createQuery("SELECT a FROM Account a WHERE a.email = :email", Account.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
