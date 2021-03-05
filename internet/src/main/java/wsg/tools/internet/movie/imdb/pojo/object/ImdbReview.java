package wsg.tools.internet.movie.imdb.pojo.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.LocalDate;
import lombok.Getter;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.movie.imdb.ImdbCreativeWork;

/**
 * IMDb reviews.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Review")
public class ImdbReview {

    private String name;
    private ImdbCreativeWork itemReviewed;
    private ImdbPerson author;
    private LocalDate dateCreated;
    private Language inLanguage;
    private String reviewBody;
    private ImdbRating reviewRating;
}
