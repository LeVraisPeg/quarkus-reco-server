package fr.univtln.pegliasco.tp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private int year;

    @OneToMany(mappedBy = "movie")
    private List<Rating> ratings;

    @ManyToMany
    private List<Gender> genders;

    @ManyToMany
    private List<Tag> tags;
}
