package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * OMDb TV episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbEpisode extends OmdbTitle {

    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("Episode")
    private Integer currentEpisode;
    private String seriesId;
}
