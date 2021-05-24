package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.MovieGenre;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * A movie subject from Douban, a movie or a series.
 *
 * @author Kingen
 * @since 2021/5/20
 */
public abstract class AbstractMovie extends AbstractSubject
    implements ImdbIdentifier, CoverSupplier {

    @JsonProperty("image")
    private String cover;
    @JsonProperty("director")
    private List<DoubanPerson> directors;
    @JsonProperty("actor")
    private List<DoubanPerson> actors;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JsonProperty("genre")
    private List<MovieGenre> genres;
    @JsonProperty("duration")
    private Duration duration;
    /**
     * Short description ending with ellipsis.
     */
    @JsonProperty("description")
    private String description;
    @JsonProperty("aggregateRating")
    private AggregateRating rating;

    private String imdbId;
    private int year;
    private String zhTitle;
    private String originalTitle;
    private boolean released;
    private List<Language> languages;
    private List<Runtime> runtimes;


    AbstractMovie() {
    }

    @Override
    public DoubanCatalog getSubtype() {
        return DoubanCatalog.MOVIE;
    }

    public String getCover() {
        return cover;
    }

    @Override
    public URL getCoverURL() {
        return NetUtils.createURL(cover);
    }

    public List<DoubanPerson> getDirectors() {
        return directors;
    }

    public List<DoubanPerson> getActors() {
        return actors;
    }

    public LocalDate getRelease() {
        return release;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public AggregateRating getRating() {
        return rating;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }

    void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public int getYear() {
        return year;
    }

    void setYear(int year) {
        this.year = year;
    }

    public String getZhTitle() {
        return zhTitle;
    }

    void setZhTitle(String zhTitle) {
        this.zhTitle = zhTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public boolean isReleased() {
        return released;
    }

    void setReleased(boolean released) {
        this.released = released;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Runtime> getRuntimes() {
        return runtimes;
    }

    void setRuntimes(List<Runtime> runtimes) {
        this.runtimes = runtimes;
    }
}
