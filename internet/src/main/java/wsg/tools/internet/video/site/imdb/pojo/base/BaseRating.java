package wsg.tools.internet.video.site.imdb.pojo.base;

import lombok.Getter;

/**
 * Rating data.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public abstract class BaseRating {
    private Double bestRating;
    private Double ratingValue;
    private Double worstRating;
}
