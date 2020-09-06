package wsg.tools.internet.video.entity.gen.base;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.lang.Money;
import wsg.tools.internet.video.entity.gen.object.GenImdbEpisode;
import wsg.tools.internet.video.entity.gen.object.GenImdbMovie;
import wsg.tools.internet.video.entity.gen.object.GenImdbSeries;
import wsg.tools.internet.video.entity.gen.object.GenPerson;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.jackson.deserializer.MultiFormatTemporalDeserializer;
import wsg.tools.internet.video.jackson.extra.PropertyNamingStrategies;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.Temporal;
import java.util.List;

/**
 * Base class of titles from PT Gen.
 *
 * @author Kingen
 * @since 2020/8/30
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GenImdbMovie.class, name = "Movie"),
        @JsonSubTypes.Type(value = GenImdbSeries.class, name = "TVSeries"),
        @JsonSubTypes.Type(value = GenImdbEpisode.class, name = "TVEpisode"),
})
public abstract class BaseGenImdbTitle extends BaseGenResponse {

    @JsonProperty("name")
    private String text;
    private List<GenPerson> actors;
    private List<TitleItem> aka;
    private List<GenPerson> creators;
    private Integer critic;
    @JsonProperty("datePublished")
    private LocalDate release;
    private String description;
    @JsonProperty("details")
    private Detail detail;
    private List<GenPerson> directors;
    private Duration duration;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    private String imdbId;
    private String imdbLink;
    private String imdbRating;
    private Double imdbRatingAverage;
    private Integer imdbVotes;
    private List<String> keywords;
    private String poster;
    private Integer popularity;
    @JsonProperty("release_date")
    private List<ReleaseItem> releaseDates;
    private Integer reviews;
    private Year year;

    @Getter
    @Setter
    public static class TitleItem {
        @JsonProperty("country")
        private RegionEnum region;
        private String title;
    }

    @Getter
    @Setter
    @JsonIgnoreProperties({"Opening Weekend USA"})
    @JsonNaming(PropertyNamingStrategies.UpperSpaceStrategy.class)
    public static class Detail {
        private static final String SEPARATOR = "|";

        @JsonProperty("Language")
        private List<LanguageEnum> languages;
        @JsonProperty("Country")
        private List<RegionEnum> regions;

        @JsonProperty("Runtime")
        private List<Duration> durations;
        private String officialSites;

        private String productionCo;
        private List<String> color;
        private String soundMix;

        private String filmingLocations;

        private String alsoKnownAs;

        private String aspectRatio;

        @JsonProperty("Release Date")
        private LocalDate release;

        private Money budget;
        @JsonProperty("Cumulative Worldwide Gross")
        private Money boxOffice;
        @JsonProperty("Gross USA")
        private Money gross;
    }

    @Getter
    @Setter
    private static class ReleaseItem {
        private RegionEnum region;
        @JsonProperty("date")
        @JsonDeserialize(using = MultiFormatTemporalDeserializer.class)
        private Temporal release;
    }
}