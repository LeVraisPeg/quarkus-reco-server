package fr.univtln.pegliasco.tp.model.nosql.Elastic;


public class RatingElastic {
    private Long id;
    private Float rate;
    private Long timestamp;
    private Long movieId;
    private Long accountId;

    public RatingElastic() {
    }

    public RatingElastic(Long id, Float rate, Long timestamp, Long movieId, Long accountId) {
        this.id = id;
        this.rate = rate;
        this.timestamp = timestamp;
        this.movieId = movieId;
        this.accountId = accountId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Float getRate() {
        return rate;
    }
    public void setRate(Float rate) {
        this.rate = rate;
    }
    public Long getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public Long getMovieId() {
        return movieId;
    }
    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }
    public Long getAccountId() {
        return accountId;
    }
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

}

