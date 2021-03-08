package wsg.tools.internet.movie.imdb;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.enums.Region;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.ImdbRating;
import wsg.tools.internet.movie.common.enums.MovieGenre;
import wsg.tools.internet.movie.common.enums.RatingSource;
import wsg.tools.internet.movie.common.jackson.JoinedValue;

/**
 * Base class of titles from OMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({@JsonSubTypes.Type(value = OmdbMovie.class, name = "movie"),
    @JsonSubTypes.Type(value = OmdbSeries.class, name = "series"),
    @JsonSubTypes.Type(value = OmdbEpisode.class, name = "episode"),})
@JsonIgnoreProperties({"Response", "Error"})
public class OmdbTitle extends BaseImdbTitle {

    private static final String SEPARATOR = ", ";

    @JsonProperty(CssSelectors.ATTR_TITLE)
    private String enTitle;
    private ImdbRating rated;
    @JsonProperty("released")
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate release;
    private Runtime runtime;
    private String plot;

    @JsonProperty("Genre")
    @JoinedValue(separator = SEPARATOR)
    private List<MovieGenre> genres;
    @JsonProperty("Director")
    @JoinedValue(separator = SEPARATOR)
    private List<String> directors;
    @JsonProperty("Writer")
    @JoinedValue(separator = SEPARATOR)
    private List<String> writers;
    @JsonProperty("Actors")
    @JoinedValue(separator = SEPARATOR)
    private List<String> actors;
    @JsonProperty("Language")
    @JoinedValue(separator = SEPARATOR)
    private List<Language> languages;
    @JsonProperty("Country")
    @JoinedValue(separator = SEPARATOR)
    private List<Region> regions;

    private String awards;
    private String poster;
    private List<Rating> ratings;
    private Integer metascore;
    private Double imdbRating;
    private Integer imdbVotes;

    OmdbTitle() {
    }

    @Getter
    static class Rating {
        private String value;
        private RatingSource source;
    }
}
