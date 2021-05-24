package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;

/**
 * One season of a series retrieved from OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
@Getter
public class OmdbSeason extends OmdbResponse {

    @JsonProperty("Title")
    private String title;
    @JsonProperty("Season")
    private int currentSeason;
    @JsonProperty("totalSeasons")
    private int seasonsCount;
    @JsonProperty("Episodes")
    @JsonDeserialize(contentAs = EpisodeIndex.class)
    private List<OmdbEpisodeIndex> episodes;
}
