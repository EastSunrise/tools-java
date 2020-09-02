package wsg.tools.internet.video.entity.gen.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.video.entity.gen.base.BaseGenResponse;
import wsg.tools.internet.video.enums.CountryEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;
import wsg.tools.internet.video.jackson.deserializer.StringOrPersonDeserializer;

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
    @JoinedValue(separator = SignConstants.SLASH)
    private List<CountryEnum> countries;
    private Duration duration;
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
    @JsonDeserialize(contentUsing = StringOrPersonDeserializer.class)
    private List<GenPerson> casts;
    @JsonProperty("director")
    @JsonDeserialize(contentUsing = StringOrPersonDeserializer.class)
    private List<GenPerson> directors;
    @JsonProperty("writer")
    @JsonDeserialize(contentUsing = StringOrPersonDeserializer.class)
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

    public Long parseDoubanLink() {
        if (StringUtils.isBlank(doubanLink)) {
            return null;
        }
        return Long.parseLong(AssertUtils.matches(DOUBAN_LINK_REGEX, doubanLink).group(1));
    }
}