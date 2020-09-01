package wsg.tools.internet.video.entity.imdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import java.time.Duration;

/**
 * IMDb TV Episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbEpisode extends BaseImdbTitle {

    private String seriesId;
    @JsonProperty("timeRequired")
    private Duration duration;
}