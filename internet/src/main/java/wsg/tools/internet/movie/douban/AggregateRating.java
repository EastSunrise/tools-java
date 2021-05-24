package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Aggregate rating data.
 *
 * @author Kingen
 * @since 2020/8/31
 */
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

    AggregateRating() {
    }

    public Integer getRatingCount() {
        return ratingCount;
    }

    public Double getBestRating() {
        return bestRating;
    }

    public Double getWorstRating() {
        return worstRating;
    }

    public Double getRatingValue() {
        return ratingValue;
    }
}