package wsg.tools.internet.video.site.douban.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

/**
 * Aggregate rating data.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("AggregateRating")
public class AggregateRating {
    @JsonProperty("ratingCount")
    private Integer ratingCount;
    @JsonProperty("bestRating")
    private Double bestRating;
    @JsonProperty("worstRating")
    private Double worstRating;
    @JsonProperty("ratingValue")
    private Double ratingValue;
}