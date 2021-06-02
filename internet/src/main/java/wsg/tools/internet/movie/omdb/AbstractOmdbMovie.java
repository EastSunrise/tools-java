package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.common.enums.Region;
import wsg.tools.internet.common.jackson.JsonJoinedValue;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.MovieGenre;
import wsg.tools.internet.movie.common.enums.RatingSource;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * A movie response returned by OMDb, a movie, a series or an episode.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public abstract class AbstractOmdbMovie extends OmdbResponse
    implements ImdbIdentifier, CoverSupplier {

    private static final String SEPARATOR = ", ";

    @JsonProperty("imdbID")
    private String id;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Rated")
    private String rated;
    @JsonProperty("Released")
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate release;
    @JsonProperty("Runtime")
    private Runtime runtime;
    @JsonProperty("Genre")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<MovieGenre> genres;
    @JsonProperty("Director")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<String> directors;
    @JsonProperty("Writer")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<String> writers;
    @JsonProperty("Actors")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<String> actors;
    @JsonProperty("Plot")
    private String plot;
    @JsonProperty("Language")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<Language> languages;
    @JsonProperty("Country")
    @JsonJoinedValue(separator = SEPARATOR)
    private List<Region> regions;
    @JsonProperty("Awards")
    private String awards;
    @JsonProperty("Poster")
    private String poster;
    @JsonProperty("Ratings")
    private List<Rating> ratings;
    @JsonProperty("Metascore")
    private Integer metaScore;
    @JsonProperty("imdbRating")
    private Double imdbRating;
    @JsonProperty("imdbVotes")
    private Integer imdbVotes;

    AbstractOmdbMovie() {
    }

    @Override
    public String getImdbId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getRated() {
        return rated;
    }

    public LocalDate getRelease() {
        return release;
    }

    public Runtime getRuntime() {
        return runtime;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public List<String> getDirectors() {
        return directors;
    }

    public List<String> getWriters() {
        return writers;
    }

    public List<String> getActors() {
        return actors;
    }

    public String getPlot() {
        return plot;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public String getAwards() {
        return awards;
    }

    @Override
    public URL getCoverURL() {
        return Optional.ofNullable(poster).map(NetUtils::createURL).orElse(null);
    }

    public List<Rating> getRatings() {
        return ratings;
    }

    public Integer getMetaScore() {
        return metaScore;
    }

    public Double getImdbRating() {
        return imdbRating;
    }

    public Integer getImdbVotes() {
        return imdbVotes;
    }

    private static class Rating {

        @JsonProperty("Source")
        private RatingSource source;
        @JsonProperty("Value")
        private String value;

        Rating() {
        }

        public RatingSource getSource() {
            return source;
        }

        public String getValue() {
            return value;
        }
    }
}
