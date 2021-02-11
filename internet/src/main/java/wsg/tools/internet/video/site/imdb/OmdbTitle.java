package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;
import wsg.tools.internet.video.site.imdb.pojo.info.RuntimeInfo;

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

    private static final String LIST_SEPARATOR = ",";

    @JsonProperty("title")
    private String text;
    private RatingEnum rated;
    @JsonProperty("released")
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate release;
    private RuntimeInfo runtime;
    @JsonProperty("Genre")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<GenreEnum> genres;
    @JsonProperty("Director")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<String> directors;
    @JsonProperty("Writer")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<String> writers;
    @JsonProperty("Actors")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<String> actors;
    private String plot;
    @JsonProperty("Language")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<LanguageEnum> languages;
    @JsonProperty("Country")
    @JoinedValue(separator = LIST_SEPARATOR)
    private List<RegionEnum> regions;
    private String awards;
    @JsonProperty("poster")
    private String posterUrl;
    private List<Rating> ratings;
    private Integer metascore;
    private Double imdbRating;
    private Integer imdbVotes;
    private String imdbId;

    OmdbTitle() {
    }

    @Getter
    static class Rating {
        private String value;
        private String source;
    }
}
