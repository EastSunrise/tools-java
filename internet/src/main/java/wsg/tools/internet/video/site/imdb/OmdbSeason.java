package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * A season of TV series.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@JsonIgnoreProperties(value = {"Response", "Error"})
public class OmdbSeason {

    private String title;
    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("TotalSeasons")
    private Integer seasonsCount;
    private List<Episode> episodes;

    @Getter
    public static class Episode {

        @JsonProperty("Episode")
        private Integer currentEpisode;
        private String imdbId;
        private String title;
        private LocalDate released;
        private Double imdbRating;
    }
}
