package wsg.tools.internet.video.entity.imdb.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.extra.AggregateRating;
import wsg.tools.internet.video.entity.imdb.object.*;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.jackson.deserializer.String2ListDeserializer;

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
})
public abstract class BaseImdbTitle {

    @JsonProperty("name")
    private String text;
    private String url;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JsonDeserialize(using = String2ListDeserializer.class)
    private List<String> keywords;
    @JsonProperty("image")
    private String posterUrl;
    /**
     * Short description ending with ellipsis.
     */
    private String description;

    /**
     * Person or organization or both.
     */
    @JsonProperty("creator")
    private List<BaseImdbObject> creators;
    @JsonProperty("director")
    private List<ImdbPerson> directors;
    @JsonProperty("actor")
    private List<ImdbPerson> actors;

    private ImdbReview review;
    @JsonProperty("contentRating")
    private RatedEnum rated;
    private AggregateRating aggregateRating;

    @JsonProperty("@context")
    private String context;
}
