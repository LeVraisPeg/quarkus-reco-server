package fr.univtln.pegliasco.tp.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class RecommendedMovies {
    private Long movieId;
    private Double rating;


}