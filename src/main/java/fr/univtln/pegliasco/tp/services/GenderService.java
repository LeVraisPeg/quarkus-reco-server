package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Gender;
import fr.univtln.pegliasco.tp.repository.GenderRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class GenderService {
    private final GenderRepository genderRepository;
    public GenderService(GenderRepository genderRepository) {
        this.genderRepository = genderRepository;
    }

    //getAllGenders
    @Transactional
    public List<Gender>getAllGenders() {
        return genderRepository.findAll();
    }

    //addGender
    @Transactional
    public void addGender(Gender gender){
        genderRepository.add(gender);
    }

    //deleteGender
    @Transactional
    public void deleteGender(Long id){
        genderRepository.delete(id);
    }
    //updateGender
    @Transactional
    public void updateGender(Long id, Gender gender) {
        Gender existingGender = genderRepository.findById(id);
        if (existingGender != null) {
            existingGender.setName(gender.getName());
            existingGender.setMovies(gender.getMovies());
            genderRepository.update(existingGender);
        }
    }

    //Recuperer gender par son id
    @Transactional
    public Gender getGenderById(Long id) {
        return genderRepository.findById(id);
    }
}
