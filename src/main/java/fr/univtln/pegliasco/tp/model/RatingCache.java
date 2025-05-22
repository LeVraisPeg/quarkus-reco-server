package fr.univtln.pegliasco.tp.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @ManyToOne
    @JsonIgnore
    private Account account;

    @ManyToOne
    @JsonIgnore
    private Movie movie;

    @Column
    private Long timestamp;

}
