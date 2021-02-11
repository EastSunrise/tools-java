package wsg.tools.internet.video.site.douban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.douban.pojo.AggregateRating;
import wsg.tools.internet.video.site.douban.pojo.DoubanPerson;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.annotation.Nullable;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DoubanSeries.class, name = "TVSeries"),
        @JsonSubTypes.Type(value = DoubanMovie.class, name = "Movie")
})
@JsonIgnoreProperties(value = "@context")
public abstract class BaseDoubanSubject implements DoubanIdentifier, ImdbIdentifier {

    private long id;
    /**
     * Combined with title and original title.
     */
    @JsonProperty("name")
    private String name;
    /**
     * Only path.
     */
    @JsonProperty("url")
    private String url;
    @JsonProperty("image")
    private String posterUrl;
    @JsonProperty("director")
    private List<DoubanPerson> directors;
    @JsonProperty("author")
    private List<DoubanPerson> authors;
    @JsonProperty("actor")
    private List<DoubanPerson> actors;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    @JsonProperty("duration")
    private Duration duration;
    /**
     * Short description ending with ellipsis.
     */
    @JsonProperty("description")
    private String description;
    @JsonProperty("aggregateRating")
    private AggregateRating rating;

    private String title;
    private String imdbId;
    private String originalTitle;
    private Year year;
    private boolean released;
    private List<LanguageEnum> languages;

    BaseDoubanSubject() {
    }

    @Override
    public Long getDbId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    @Nullable
    @Override
    public String getImdbId() {
        return imdbId;
    }

    void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    void setYear(Year year) {
        this.year = year;
    }

    void setReleased(boolean released) {
        this.released = released;
    }

    @Nullable
    public List<LanguageEnum> getLanguages() {
        return languages;
    }

    void setLanguages(List<LanguageEnum> languages) {
        this.languages = languages;
    }
}
