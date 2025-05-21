package fr.univtln.pegliasco.tp.model.nosql;

import java.util.List;

public class MovieElastic {
    private String id;
    private String title;
    private int year;
    private String director;
    private List<String> writers;
    private List<String> actors;
    private String plot;
    private String country;
    private int runtime;
    private List<String> genders;
    private String poster;
    private List<String> tags;

    public MovieElastic() {
    }

    public MovieElastic(String id, String title, int year, String director,List<String> writers,List<String> actors,
                        String plot, String country, int runtime, List<String> genders,String poster, List<String> tags) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.runtime = runtime;
        this.director = director;
        this.actors = actors;
        this.writers = writers;
        this.plot = plot;
        this.country = country;
        this.genders = genders;
        this.poster = poster;
        this.tags = tags;
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getPlot() {
        return plot;
    }

    public void setPlot(String plot) {
        this.plot = plot;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<String> getGenders() {
        return genders;
    }

    public void setGenders(List<String> genders) {
        this.genders = genders;
    }

    public String getPoster() {
        return poster;
    }
    public void setPoster(String poster) {
        this.poster = poster;
    }
    public List<String> getTags() {
        return tags;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public List<String> getWriters() {
        return writers;
    }
    public void setWriters(List<String> writers) {
        this.writers = writers;
    }
    public List<String> getActors() {
        return actors;
    }
    public void setActors(List<String> actors) {
        this.actors = actors;
    }
}
