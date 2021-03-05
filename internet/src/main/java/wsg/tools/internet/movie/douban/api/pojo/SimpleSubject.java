package wsg.tools.internet.movie.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.movie.common.enums.DoubanSubtype;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * A simple subject.
 *
 * @author Kingen
 * @since 2020/7/27
 */
@Getter
public class SimpleSubject {

    private Long id;
    private DoubanSubtype subtype;
    private String title;
    private String originalTitle;
    private String alt;
    private Year year;
    private List<MovieGenre> genres;
    private List<Duration> durations;

    @JsonProperty("images")
    private Image image;
    private Rating rating;
    @JsonProperty("mainland_pubdate")
    private LocalDate mainlandRelease;
    @JsonProperty("pubdates")
    private List<LocalDate> releases;
    private Integer collectCount;
    private List<SimpleCelebrity> casts;
    private List<SimpleCelebrity> directors;
    private Boolean hasVideo;
}
