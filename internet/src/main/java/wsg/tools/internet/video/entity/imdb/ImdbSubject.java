package wsg.tools.internet.video.entity.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.lang.Money;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Subjects from omdb api.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Setter
@Getter
public class ImdbSubject {

    private String imdbId;
    private ImdbTypeEnum type;
    @JsonProperty("title")
    private String text;
    private Year year;
    private RatedEnum rated;
    private LocalDate released;
    @JsonDeserialize(using = DurationExtDeserializer.class)
    private Duration runtime;
    @JsonProperty("Genre")
    private List<GenreEnum> genres;
    private String plot;
    @JsonProperty("Language")
    private List<LanguageEnum> languages;
    @JsonProperty("Country")
    private List<CountryEnum> countries;
    @JsonProperty("TotalSeasons")
    private Integer seasonsCount;
    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("Episode")
    private Integer currentEpisode;
    private String seriesId;

    @JsonProperty("Director")
    private List<String> directors;
    @JsonProperty("Writer")
    private List<String> writers;
    @JsonProperty("Actors")
    private List<String> actors;
    private String awards;
    private String poster;
    private String website;

    private LocalDate dvd;
    private Money boxOffice;
    private String production;
    private Integer metascore;
    private Double imdbRating;
    private Long imdbVotes;
    private List<Rating> ratings;

    @Setter
    @Getter
    static class Rating {
        private String value;
        private String source;
    }
}
