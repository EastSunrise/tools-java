package wsg.tools.internet.video.entity;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.lang.Money;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.deserializer.CollectionDeserializer;
import wsg.tools.internet.video.jackson.deserializer.DurationExtDeserializer;
import wsg.tools.internet.video.jackson.deserializer.ReleaseDeserializer;
import wsg.tools.internet.video.jackson.deserializer.YearExtDeserializer;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Map;

/**
 * Object of a subject.
 *
 * @author Kingen
 * @since 2020/6/27
 */
@Setter
@Getter
@JsonIgnoreProperties({"collection", "Director", "Writer", "Actors"})
public class Subject {

    @JsonAlias("id")
    private Long dbId;
    private String imdbId;

    private RecordEnum record;
    private LocalDate tagDate;

    private String title;
    @JsonAlias("Type")
    private SubtypeEnum subtype;
    private String text;
    private String originalTitle;
    @JsonDeserialize(using = YearExtDeserializer.class)
    private Year year;
    private String alt;
    @JsonAlias("Genre")
    @JsonDeserialize(using = CollectionDeserializer.class)
    private List<GenreEnum> genres;
    @JsonAlias("Country")
    @JsonDeserialize(using = CollectionDeserializer.class)
    private List<Country> countries;
    @JsonAlias("Language")
    @JsonDeserialize(using = CollectionDeserializer.class)
    private List<Language> languages;
    /**
     * todo merge
     */
    @JsonAlias({"pubdate", "Released"})
    private LocalDate pubDate;
    @JsonAlias("mainland_pubdate")
    private LocalDate mainlandPubDate;
    @JsonAlias("pubdates")
    @JsonDeserialize(contentUsing = ReleaseDeserializer.class)
    private List<LocalDate> releases;
    @JsonDeserialize(using = DurationExtDeserializer.class)
    private Duration runtime;
    @JsonDeserialize(contentUsing = DurationExtDeserializer.class)
    private List<Duration> durations;
    private List<String> aka;
    private List<String> tags;
    private RatedEnum rated;
    private Integer episodesCount;
    @JsonAlias("totalSeasons")
    private Integer seasonsCount;
    @JsonAlias("Season")
    private Integer currentSeason;
    @JsonAlias("Episode")
    private Integer currentEpisode;
    private String seriesId;
    private String plot;
    private String summary;

    private List<Celebrity> directors;
    private List<Celebrity> writers;
    private List<Celebrity> casts;
    @JsonAlias(value = "images")
    private Image image;
    private String poster;
    private String website;
    private String doubanSite;
    private String mobileUrl;
    private String shareUrl;
    private Boolean hasSchedule;
    private String scheduleUrl;
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
    private Boolean hasTicket;

    private Integer collectCount;
    private Integer doCount;
    private Integer wishCount;
    private Integer ratingsCount;
    private Rating rating;
    private Integer reviewsCount;
    private List<Review> popularReviews;
    private Integer commentsCount;
    private List<Comment> popularComments;

    @JsonAlias("Ratings")
    private List<ExtRating> extRatings;
    private Integer metascore;
    private Double imdbRating;
    private Long imdbVotes;
    private String awards;
    private LocalDate dvd;
    private Money boxOffice;
    private String production;

    @Setter
    @Getter
    private static class Photo {
        private String thumb;
        private String image;
        private String cover;
        private String alt;
        private Long id;
        private String icon;
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

    @Setter
    @Getter
    private static class Review {
        private Rating rating;
        private String title;
        private Long subjectId;
        private User author;
        private String summary;
        private String alt;
        private Long id;
    }

    @Setter
    @Getter
    private static class User {
        private String uid;
        private String avatar;
        private String signature;
        private String alt;
        private Long id;
        private String name;
    }

    @Setter
    @Getter
    private static class Comment {
        private Rating rating;
        private Integer usefulCount;
        private User author;
        private Long subjectId;
        private String content;
        private LocalDateTime createdAt;
        private Long id;
    }

    @Setter
    @Getter
    private static class Rating {
        private Integer max;
        private Double average;
        private Map<Integer, Integer> details;
        private Integer stars;
        private Integer min;
        private Integer value;
    }

    @Setter
    @Getter
    static class ExtRating {
        private String value;
        private String source;
    }


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

}
