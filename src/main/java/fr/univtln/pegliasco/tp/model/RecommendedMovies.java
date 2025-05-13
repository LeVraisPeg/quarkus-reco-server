package fr.univtln.pegliasco.tp.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Entity
public class RecommendedMovies {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(name = "recommended_movies", joinColumns = @JoinColumn(name = "recommended_movies_id"))
    @MapKeyColumn(name = "movie_id")
    @Column(name = "rating")
    private Map<Long, Double> recommendedMovies = new HashMap<>();
}