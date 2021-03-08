package wsg.tools.internet.movie.imdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.common.CssSelectors;

/**
 * A season of TV series.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@JsonIgnoreProperties({"Response", "Error"})
public class OmdbSeason {

    @JsonProperty(CssSelectors.ATTR_TITLE)
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
