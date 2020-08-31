package wsg.tools.internet.video.entity.imdb.base;

import lombok.Getter;
import lombok.Setter;

/**
 * Rating data.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public abstract class BaseRating {
    private Double bestRating;
    private Double ratingValue;
    private Double worstRating;
}
