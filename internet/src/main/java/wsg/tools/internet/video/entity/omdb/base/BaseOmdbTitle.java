package wsg.tools.internet.video.entity.omdb.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.omdb.object.OmdbEpisode;
import wsg.tools.internet.video.entity.omdb.object.OmdbMovie;
import wsg.tools.internet.video.entity.omdb.object.OmdbSeries;
import wsg.tools.internet.video.enums.CountryEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Base class of titles from OMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = OmdbMovie.class, name = "movie"),
        @JsonSubTypes.Type(value = OmdbSeries.class, name = "series"),
        @JsonSubTypes.Type(value = OmdbEpisode.class, name = "episode"),
})
public abstract class BaseOmdbTitle extends BaseOmdbResponse {

    @JsonProperty("title")
    private String text;
    private Year year;
    private RatedEnum rated;
    @JsonProperty("released")
    private LocalDate release;
    @JsonProperty("runtime")
    @JsonDeserialize(using = DurationExtDeserializer.class)
    private Duration duration;
    @JsonProperty("Genre")
    private List<GenreEnum> genres;
    @JsonProperty("Director")
    private List<String> directors;
    @JsonProperty("Writer")
    private List<String> writers;
    @JsonProperty("Actors")
    private List<String> actors;
    private String plot;
    @JsonProperty("Language")
    private List<LanguageEnum> languages;
    @JsonProperty("Country")
    private List<CountryEnum> countries;
    private String awards;
    @JsonProperty("poster")
    private String posterUrl;
    private List<Rating> ratings;
    private Integer metascore;
    private Double imdbRating;
    private Long imdbVotes;
    private String imdbId;

    @Setter
    @Getter
    private static class Rating {
        private String value;
        private String source;
    }
}
