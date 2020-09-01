package wsg.tools.internet.video.entity.douban.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.CountryEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Info of a subject.
 *
 * @author Kingen
 * @since 2020/7/31
 */
@Getter
@Setter
public class ImdbInfo {
    @JsonProperty("id")
    private String apiAlt;
    private String title;
    private String alt;
    private String altTitle;
    private String mobileLink;
    private String image;
    private String summary;
    private List<SimpleCelebrity> author;
    private Rating rating;
    private Info attrs;
    private List<Tag> tags;

    @Getter
    @Setter
    private static class Info {
        private List<String> title;
        private List<Year> year;
        @JsonProperty("country")
        private List<CountryEnum> countries;
        @JsonProperty("language")
        private List<LanguageEnum> languages;
        @JsonProperty("movie_duration")
        private List<Duration> durations;
        @JsonProperty("movie_type")
        private List<GenreEnum> genres;
        @JsonProperty("pubdate")
        private List<LocalDate> releases;
        @JsonProperty("cast")
        private List<String> casts;
        @JsonProperty("director")
        private List<String> directors;
        @JsonProperty("writer")
        private List<String> writers;
    }

    @Setter
    @Getter
    private static class Tag {
        private Integer count;
        private String name;
    }
}