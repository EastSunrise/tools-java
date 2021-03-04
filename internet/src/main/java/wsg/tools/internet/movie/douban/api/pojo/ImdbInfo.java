package wsg.tools.internet.movie.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import wsg.tools.internet.movie.common.enums.GenreEnum;
import wsg.tools.internet.movie.common.enums.LanguageEnum;
import wsg.tools.internet.movie.common.enums.RegionEnum;

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
    private static class Info {
        private List<String> title;
        private List<Year> year;
        @JsonProperty("country")
        private List<RegionEnum> regions;
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

    @Getter
    private static class Tag {
        private Integer count;
        private String name;
    }
}