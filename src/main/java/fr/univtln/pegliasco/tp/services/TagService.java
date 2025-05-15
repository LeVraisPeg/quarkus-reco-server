package fr.univtln.pegliasco.tp.services;


import fr.univtln.pegliasco.tp.model.Movie;
import fr.univtln.pegliasco.tp.model.Tag;
import fr.univtln.pegliasco.tp.model.User;
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
        List<Tag> tags = tagRepository.findAll();
        tags.forEach(tag -> {
            if (tag.getAccount() instanceof User user && user.getTags() != null) {
                user.getTags().size();
            }
        });
        return tags;
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

    //saveOrUpdate
    @Transactional
    public void saveOrUpdate(Tag tag) {
        if (tag.getId() == null) {
            tagRepository.add(tag);
        } else {
            Tag existingTag = tagRepository.findById(tag.getId());
            if (existingTag != null) {
                existingTag.setName(tag.getName());
                existingTag.setMovies(tag.getMovies());
                tagRepository.update(existingTag);
            }
        }
    }

    // Récupérer les films par tag
    @Transactional
    public List<Movie> getMoviesByTag(Long id) {
        Tag tag = tagRepository.findById(id);
        if (tag != null) {
            return tag.getMovies();
        } else {
            return null;
        }
    }



}
