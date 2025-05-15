package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Account;
import fr.univtln.pegliasco.tp.repository.AccountRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
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
            String email;
            do {
                String nom = faker.name().lastName();
                String prenom = faker.name().firstName();
                email = prenom.toLowerCase() + "." + nom.toLowerCase() + "@example.com";
            } while (accountRepository.findByEmail(email) != null);

            account = new Account();
            account.setId(id);
            account.setNom(faker.name().lastName());
            account.setPrenom(faker.name().firstName());
            account.setEmail(email);
            account.setPassword(faker.internet().password(8, 12, true, true));
            account.setRole(Account.Role.USER);
            accountRepository.persist(account);
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


}
