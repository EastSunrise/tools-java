package wsg.tools.internet.video.site.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;

import java.time.LocalDate;
import java.util.List;

/**
 * A subject from douban
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Getter
@Setter
@JsonIgnoreProperties("collection")
public class Subject extends SimpleSubject {

    private List<String> aka;
    @JsonProperty("pubdate")
    private LocalDate release;

    private List<LanguageEnum> languages;
    private List<RegionEnum> regions;
    private Integer episodesCount;
    private Integer seasonsCount;
    private Integer currentSeason;

    private String summary;
    private List<String> tags;
    private Integer doCount;
    private Integer wishCount;
    private Integer ratingsCount;
    private Integer reviewsCount;
    private List<Review> popularReviews;
    private Integer commentsCount;
    private List<Comment> popularComments;
    private List<SimpleCelebrity> writers;

    private Integer photosCount;
    private List<Photo> photos;
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
