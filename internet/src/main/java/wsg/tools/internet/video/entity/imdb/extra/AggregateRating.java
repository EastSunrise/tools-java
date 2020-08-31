package wsg.tools.internet.video.entity.imdb.extra;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseRating;

/**
 * Aggregate rating data.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("AggregateRating")
public class AggregateRating extends BaseRating {

    private Integer ratingCount;
}