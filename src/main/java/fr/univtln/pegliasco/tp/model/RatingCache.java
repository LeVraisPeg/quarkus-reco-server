package fr.univtln.pegliasco.tp.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class RatingCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Float rate;

    @Column
    private Long accountId;

    @Column
    private Long movieId;
}
