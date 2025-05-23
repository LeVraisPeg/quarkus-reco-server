package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Account;
import fr.univtln.pegliasco.tp.model.Rating;
import fr.univtln.pegliasco.tp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;

import com.github.javafaker.Faker;



@ApplicationScoped
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    // Récupérer tous les comptes
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    // Récupérer un compte par son ID
    public Account getAccountById(Long id) {
        return accountRepository.findById(id);
    }
    // Récupérer un compte par son nom d'utilisateur
    public Account getAccountByNom(String nom) {
        return accountRepository.findByNom(nom);
    }
    // Ajouter un compte
    @Transactional
    public void addAccount(Account account) {
        accountRepository.persist(account);
    }
    // Mettre à jour un compte
    @Transactional
    public void updateAccount(Account account) {
        accountRepository.update(account);
    }

    // Supprimer un compte
    @Transactional
    public void deleteAccount(Long id) {
        Account existingAccount = accountRepository.findById(id);
        if (existingAccount != null) {
            accountRepository.delete(existingAccount);
        }
    }
    // Vérifier les informations du compte (email et password)
    public Account authenticate(String nom, String password) {
        return accountRepository.findByNomAndPassword(nom, password);
    }

    // Récupérer un compte par son rôle
    public List<Account> getAccountByRole(String role) {
        return accountRepository.findByRole(role);
    }

    //find by email
    public Account findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    //find or create account by id

    @Transactional
    public Account findOrCreateById(Long id) {
        Account account = accountRepository.findById(id);
        if (account == null) {
            Faker faker = new Faker();
            int maxTries = 10;
            for (int i = 0; i < maxTries; i++) {
                String nom = faker.name().lastName();
                String prenom = faker.name().firstName();
                int randomNum = (int) (Math.random() * 100_000);
                String uniqueNom = nom + "_" + prenom + "_" + randomNum;
                String uniqueEmail = prenom.toLowerCase() + "." + nom.toLowerCase() + randomNum + "@example.com";
                account = new Account();
                account.setId(id);
                account.setNom(uniqueNom);
                account.setPrenom(prenom);
                account.setEmail(uniqueEmail);
                account.setPassword(faker.internet().password(8, 12, true, true));
                account.setRole(Account.Role.USER);
                try {
                    accountRepository.persist(account);
                    return account;
                } catch (Exception e) {
                    // Collision d’unicité, on réessaie
                }
            }
            throw new RuntimeException("Impossible de générer un nom/email unique après " + maxTries + " essais");
        }
        return account;
    }

    //findAllAsMap
    public Map<Long, Account> findAllAsMap() {
        return accountRepository.findAllAsMap();
    }

    //find by id
    public Account findById(Long id) {
        return accountRepository.findById(id);
    }


    public List<Rating> getRatingsByAccountId(Long id) {
        Account account = accountRepository.findById(id);
        if (account != null) {
            return account.getRatings();
        }
        return null;
    }


}
