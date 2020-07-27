package wsg.tools.internet.video.entity.imdb;

import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonAlias("title")
    private String text;
    private Year year;
    private RatedEnum rated;
    private LocalDate released;
    @JsonDeserialize(using = DurationExtDeserializer.class)
    private Duration runtime;
    @JsonAlias("Genre")
    private List<GenreEnum> genres;
    private String plot;
    @JsonAlias("Language")
    private List<LanguageEnum> languages;
    @JsonAlias("Country")
    private List<CountryEnum> countries;
    @JsonAlias("TotalSeasons")
    private Integer seasonsCount;
    @JsonAlias("Season")
    private Integer currentSeason;
    @JsonAlias("Episode")
    private Integer currentEpisode;
    private String seriesId;

    @JsonAlias("Director")
    private List<String> directors;
    @JsonAlias("Writer")
    private List<String> writers;
    @JsonAlias("Actor")
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
