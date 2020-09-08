package wsg.tools.internet.video.entity.omdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbResponse;

import java.time.LocalDate;
import java.util.List;

/**
 * A season of TV series.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class OmdbSeason extends BaseOmdbResponse {

    private String title;
    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("TotalSeasons")
    private Integer seasonsCount;
    private List<Episode> episodes;

    @Getter
    @Setter
    public static class Episode {

        @JsonProperty("Episode")
        private Integer currentEpisode;
        private String imdbId;
        private String title;
        private LocalDate released;
        private Double imdbRating;
    }
}
