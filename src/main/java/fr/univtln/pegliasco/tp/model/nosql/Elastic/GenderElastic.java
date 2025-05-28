package fr.univtln.pegliasco.tp.model.nosql.Elastic;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.univtln.pegliasco.tp.model.Movie;

import java.util.List;

public class GenderElastic {
    private Long id;
    private String name;
    @JsonIgnore
    private List<Long> moviesIds;

    public GenderElastic() {
    }
    public GenderElastic(Long id, String name, List<Long> moviesIds) {
        this.id = id;
        this.name = name;
        this.moviesIds = moviesIds;
    }
    // Getters et Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Long> getMovies() {
        return moviesIds;
    }
    public void setMovies(List<Long> moviesIds) {
        this.moviesIds = moviesIds;
    }

}
