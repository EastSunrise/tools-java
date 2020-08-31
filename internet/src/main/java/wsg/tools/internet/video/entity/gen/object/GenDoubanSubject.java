package wsg.tools.internet.video.entity.gen.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.gen.base.BaseGenResponse;
import wsg.tools.internet.video.enums.CountryEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;
import wsg.tools.internet.video.jackson.deserializer.ReleaseDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Douban subject from PT Gen.
 *
 * @author Kingen
 * @since 2020/8/30
 */
@Getter
@Setter
public class GenDoubanSubject extends BaseGenResponse {

    @JsonProperty("chinese_title")
    private String title;
    @JsonProperty("foreign_title")
    private String originalTitle;
    private List<String> thisTitle;
    private String imdbId;
    private Year year;
    @JsonProperty("language")
    private List<LanguageEnum> languages;
    @JsonProperty("region")
    private List<CountryEnum> countries;
    @JsonDeserialize(using = DurationExtDeserializer.class)
    private Duration duration;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    private List<String> aka;

    @JsonProperty("playdate")
    @JsonDeserialize(contentUsing = ReleaseDeserializer.class)
    private List<LocalDate> releases;

    private String doubanLink;

    private String introduction;
    private List<String> transTitle;
    private String imdbLink;

    @JsonProperty("cast")
    private List<String> casts;
    @JsonProperty("director")
    private List<String> directors;
    @JsonProperty("writer")
    private List<String> writers;
    private String doubanRating;
    private int doubanVotes;
    private double doubanRatingAverage;
    private String imdbRating;
    private int imdbVotes;
    private double imdbRatingAverage;
    private List<String> tags;

    @JsonProperty("episodes")
    private Integer episodesCount;

    private String awards;
    private String poster;
}