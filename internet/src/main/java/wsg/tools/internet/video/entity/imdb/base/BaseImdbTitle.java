package wsg.tools.internet.video.entity.imdb.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.extra.AggregateRating;
import wsg.tools.internet.video.entity.imdb.object.*;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * Base class of titles from IMDb.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImdbSeries.class, name = "TVSeries"),
        @JsonSubTypes.Type(value = ImdbEpisode.class, name = "TVEpisode"),
        @JsonSubTypes.Type(value = ImdbMovie.class, name = "Movie"),
        @JsonSubTypes.Type(value = ImdbCreativeWork.class, name = "CreativeWork"),
})
public abstract class BaseImdbTitle {

    @JsonProperty("name")
    private String text;
    private String url;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JoinedValue(separator = ",")
    private List<String> keywords;
    @JsonProperty("image")
    private String posterUrl;

    private List<Duration> runtimes;
    private List<LanguageEnum> languages;

    /**
     * Short description ending with ellipsis.
     */
    private String description;

    /**
     * Person or organization or both.
     */
    @JsonProperty("creator")
    private List<BaseImdbObject> creators;
    @JsonProperty("actor")
    private List<ImdbPerson> actors;

    private ImdbReview review;
    private RatingEnum contentRating;
    private AggregateRating aggregateRating;

    @JsonProperty("@context")
    private String context;
}
