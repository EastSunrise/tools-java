package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbObject;
import wsg.tools.internet.video.site.imdb.pojo.object.ImdbPerson;

import javax.annotation.Nullable;
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
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = ImdbSeries.class, name = "TVSeries"),
        @JsonSubTypes.Type(value = ImdbEpisode.class, name = "TVEpisode"),
        @JsonSubTypes.Type(value = ImdbMovie.class, name = "Movie"),
        @JsonSubTypes.Type(value = ImdbCreativeWork.class, name = "CreativeWork"),
})
public class ImdbTitle extends BaseImdbTitle {

    @JsonProperty("name")
    private String text;
    @JsonProperty("url")
    private String url;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    @JsonProperty("datePublished")
    private LocalDate release;
    @JsonProperty("duration")
    private Duration duration;
    @JoinedValue(separator = ",")
    private List<String> keywords;
    @JsonProperty("image")
    private String posterUrl;
    @JsonProperty("description")
    private String description;
    @JsonProperty("director")
    private List<ImdbPerson> directors;
    @JsonProperty("creator")
    private List<BaseImdbObject> creators;
    @JsonProperty("actor")
    private List<ImdbPerson> actors;
    @JsonProperty("@context")
    private String context;

    private List<Duration> runtimes;
    private List<LanguageEnum> languages;
    private List<LocalDate> releases;

    ImdbTitle() {
    }

    @Nullable
    public List<LocalDate> getReleases() {
        return releases;
    }

    void setReleases(List<LocalDate> releases) {
        this.releases = releases;
    }

    @Nullable
    public LocalDate getRelease() {
        return release;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    void setText(String text) {
        this.text = text;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Nullable
    public List<Duration> getRuntimes() {
        return runtimes;
    }

    void setRuntimes(List<Duration> runtimes) {
        this.runtimes = runtimes;
    }

    @Nullable
    public List<LanguageEnum> getLanguages() {
        return languages;
    }

    void setLanguages(List<LanguageEnum> languages) {
        this.languages = languages;
    }
}
