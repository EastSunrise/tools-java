package wsg.tools.internet.video.entity.imdb.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import java.time.Duration;

/**
 * IMDb Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbMovie extends BaseImdbTitle {

    private Duration duration;
}
