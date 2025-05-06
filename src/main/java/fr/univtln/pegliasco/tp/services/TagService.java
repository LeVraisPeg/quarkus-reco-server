package fr.univtln.pegliasco.tp.services;

import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.repository.TagRepository;
import jakarta.transaction.Transactional;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;


@ApplicationScoped
public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    // Récupérer tous les tags
    @Transactional
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    // Ajouter un tag
    @Transactional
    public void addTag(Tag tag) {
        tagRepository.add(tag);
    }

    // Supprimer un tag par son ID
    @Transactional
    public void deleteTag(Long id) {
        tagRepository.delete(id);
    }

    // Mettre à jour un tag par son ID
    @Transactional
    public void updateTag(Long id, Tag tag) {
        Tag existingTag = tagRepository.findById(id);
        if (existingTag != null) {
            existingTag.setName(tag.getName());
            existingTag.setMovies(tag.getMovies());
            tagRepository.update(existingTag);
        }
    }

    // Récupérer un tag par son ID
    @Transactional
    public Tag getTagById(Long id) {
        return tagRepository.findById(id);
    }
}
