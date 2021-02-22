package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import wsg.tools.internet.video.common.Runtime;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.JoinedValue;

import java.time.LocalDate;
import java.util.List;

/**
 * Base class of titles from OMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OmdbMovie.class, name = "movie"),
        @JsonSubTypes.Type(value = OmdbSeries.class, name = "series"),
        @JsonSubTypes.Type(value = OmdbEpisode.class, name = "episode"),
})
@JsonIgnoreProperties(value = {"Response", "Error"})
public class OmdbTitle extends BaseImdbTitle {

    @JsonProperty("title")
    private String enTitle;
    private Integer year;
    private RatingEnum rated;
    @JsonProperty("released")
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate release;
    private Runtime runtime;
    private String plot;

    @JsonProperty("Genre")
    @JoinedValue
    private List<GenreEnum> genres;
    @JsonProperty("Director")
    @JoinedValue
    private List<String> directors;
    @JsonProperty("Writer")
    @JoinedValue
    private List<String> writers;
    @JsonProperty("Actors")
    @JoinedValue
    private List<String> actors;
    @JsonProperty("Language")
    @JoinedValue
    private List<LanguageEnum> languages;
    @JsonProperty("Country")
    @JoinedValue
    private List<RegionEnum> regions;

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
