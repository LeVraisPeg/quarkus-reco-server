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
    @JoinColumn(name = "account_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Movie movie;

}
