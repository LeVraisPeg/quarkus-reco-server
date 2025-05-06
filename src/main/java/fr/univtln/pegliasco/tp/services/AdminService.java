package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Admin;
import fr.univtln.pegliasco.tp.repository.AdminRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class AdminService {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

}
