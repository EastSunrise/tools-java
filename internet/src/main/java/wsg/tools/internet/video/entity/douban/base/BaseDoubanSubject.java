package wsg.tools.internet.video.entity.douban.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.object.DoubanMovie;
import wsg.tools.internet.video.entity.douban.object.DoubanPerson;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.imdb.extra.AggregateRating;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Base class of subjects from douban.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoubanSeries.class, name = "TVSeries"),
        @JsonSubTypes.Type(value = DoubanMovie.class, name = "Movie")
})
public abstract class BaseDoubanSubject {

    /**
     * Extra info
     */
    private String imdbId;
    private String title;
    private String originalTitle;
    private Year year;
    private List<LanguageEnum> languages;
    /**
     * Only for movies
     */
    private List<Duration> extDurations;

    /**
     * Combined with title and original title.
     */
    private String name;
    /**
     * Only path.
     */
    private String url;
    @JsonProperty("image")
    private String posterUrl;
    /**
     * Short description ending with ellipsis.
     */
    private String description;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    private Duration duration;

    private AggregateRating aggregateRating;

    @JsonProperty("@context")
    private String context;
    @JsonProperty("author")
    private List<DoubanPerson> authors;
    @JsonProperty("director")
    private List<DoubanPerson> directors;
    @JsonProperty("actor")
    private List<DoubanPerson> actors;
}
