package wsg.tools.internet.video.entity.omdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;

import java.time.Year;

/**
 * OMDb TV episode.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class OmdbEpisode extends BaseOmdbTitle {

    private Year year;

    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("Episode")
    private Integer currentEpisode;
    private String seriesId;
}
