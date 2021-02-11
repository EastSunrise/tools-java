package wsg.tools.internet.video.site.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.SubtypeEnum;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * A simple subject.
 *
 * @author Kingen
 * @since 2020/7/27
 */
@Getter
@Setter
public class SimpleSubject {

    private Long id;
    private SubtypeEnum subtype;
    private String title;
    private String originalTitle;
    private String alt;
    private Year year;
    private List<GenreEnum> genres;
    private List<Duration> durations;

    @JsonProperty(value = "images")
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
