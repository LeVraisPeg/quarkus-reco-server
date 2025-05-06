package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.User;
import fr.univtln.pegliasco.tp.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class UserService {
    private final  UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

}
