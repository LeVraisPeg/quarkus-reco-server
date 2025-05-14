package fr.univtln.pegliasco.tp.movies_to_recommend;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendedMovie {
    private Long movieId;
    private Double rate;
}