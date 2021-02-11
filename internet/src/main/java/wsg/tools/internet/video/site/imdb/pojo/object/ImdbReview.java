package wsg.tools.internet.video.site.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.imdb.ImdbCreativeWork;
import wsg.tools.internet.video.site.imdb.pojo.extra.ImdbRating;

import java.time.LocalDate;

/**
 * IMDb reviews.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Review")
public class ImdbReview {

    private String name;
    private ImdbCreativeWork itemReviewed;
    private ImdbPerson author;
    private LocalDate dateCreated;
    private LanguageEnum inLanguage;
    private String reviewBody;
    private ImdbRating reviewRating;
}
