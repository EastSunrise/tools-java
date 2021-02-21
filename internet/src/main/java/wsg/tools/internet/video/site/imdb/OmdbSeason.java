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

    @JsonProperty("title")
    private String enTitle;
    @JsonProperty("Season")
    private Integer currentSeason;
    private Integer totalSeasons;
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
