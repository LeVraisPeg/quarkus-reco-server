package fr.univtln.pegliasco.tp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int rate;

    @ManyToOne
    private User user;

    @ManyToOne
    private Movie movie;
}
