package wsg.tools.internet.video.entity.douban;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;
import wsg.tools.internet.video.jackson.deserializer.ReleaseDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;

/**
 * Subjects from douban
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Getter
@Setter
@JsonIgnoreProperties("collection")
public class DoubanSubject extends Subject {
    @JsonAlias("id")
    private Long dbId;
    private String imdbId;
    private SubtypeEnum subtype;

    private MarkEnum mark;
    private LocalDate markDate;

    private String title;
    private String originalTitle;
    private List<String> aka;
    private Year year;
    private String alt;
    private List<GenreEnum> genres;
    @JsonAlias({"pubdate"})
    private LocalDate release;
    @JsonAlias("mainland_pubdate")
    private LocalDate mainlandRelease;
    @JsonAlias("pubdates")
    @JsonDeserialize(contentUsing = ReleaseDeserializer.class)
    private List<LocalDate> releases;
    private List<LanguageEnum> languages;
    private List<CountryEnum> countries;
    @JsonDeserialize(contentUsing = DurationExtDeserializer.class)
    private List<Duration> durations;
    private Integer episodesCount;
    private Integer seasonsCount;
    private Integer currentSeason;

    private String summary;
    private List<String> tags;
    private Rating rating;
    private Integer collectCount;
    private Integer doCount;
    private Integer wishCount;
    private Integer ratingsCount;
    private Integer reviewsCount;
    private List<Review> popularReviews;
    private Integer commentsCount;
    private List<Comment> popularComments;
    private List<Celebrity> directors;
    private List<Celebrity> writers;
    private List<Celebrity> casts;

    @JsonAlias(value = "images")
    private Image image;
    private Integer photosCount;
    private List<Photo> photos;
    private Boolean hasVideo;
    private List<Video> videos;
    private List<String> blooperUrls;
    private List<ShortItem> bloopers;
    private List<String> trailerUrls;
    private List<ShortItem> trailers;
    private List<String> clipUrls;
    private List<ShortItem> clips;
    private String doubanSite;
    private String mobileUrl;
    private String shareUrl;
    private Boolean hasSchedule;
    private Boolean hasTicket;
    private String scheduleUrl;
    private String website;

    @Setter
    @Getter
    private static class Video {
        private Source source;
        private String sampleLink;
        private String videoId;
        private Boolean needPay;

        @Setter
        @Getter
        private static class Source {
            private String literal;
            private String pic;
            private String name;
        }
    }

    /**
     * for trailer and bloop
     */
    @Setter
    @Getter
    private static class ShortItem {
        private String medium;
        private String title;
        private Long subjectId;
        private String alt;
        private String small;
        private String resourceUrl;
        private Long id;
    }
}
