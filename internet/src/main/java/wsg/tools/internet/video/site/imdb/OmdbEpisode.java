package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.Year;

/**
 * OMDb TV episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbEpisode extends OmdbTitle {

    private Year year;

    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("Episode")
    private Integer currentEpisode;
    private String seriesId;
}
