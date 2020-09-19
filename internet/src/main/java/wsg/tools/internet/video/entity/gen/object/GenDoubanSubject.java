package wsg.tools.internet.video.entity.gen.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.internet.video.entity.gen.base.BaseGenResponse;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Douban subject from PT Gen.
 *
 * @author Kingen
 * @since 2020/8/30
 */
@Getter
@Setter
public class GenDoubanSubject extends BaseGenResponse {

    private static final Pattern DOUBAN_LINK_REGEX = Pattern.compile("https://movie.douban.com/subject/(\\d+)/");

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
    @JoinedValue(separator = SignEnum.SLASH)
    private List<RegionEnum> regions;
    @JsonProperty("duration")
    @JoinedValue(separator = SignEnum.SLASH)
    private List<Duration> runtimes;
    @JsonProperty("genre")
    private List<GenreEnum> genres;
    private List<String> aka;

    @JsonProperty("playdate")
    private List<LocalDate> releases;

    private String doubanLink;

    private String introduction;
    private List<String> transTitle;
    private String imdbLink;

    @JsonProperty("cast")
    private List<GenPerson> casts;
    @JsonProperty("director")
    private List<GenPerson> directors;
    @JsonProperty("writer")
    private List<GenPerson> writers;
    private String doubanRating;
    private Integer doubanVotes;
    private Double doubanRatingAverage;
    private String imdbRating;
    private Integer imdbVotes;
    private Double imdbRatingAverage;
    private List<String> tags;

    @JsonProperty("episodes")
    private Integer episodesCount;

    private String awards;
    private String poster;
}