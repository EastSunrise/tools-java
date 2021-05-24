package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * An episode retrieved from OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbEpisode extends AbstractOmdbMovie implements OmdbEpisodeIndex {

    @JsonProperty("Year")
    private Integer year;
    @JsonProperty("Season")
    private Integer currentSeason;
    @JsonProperty("Episode")
    private int currentEpisode;
    @JsonProperty("seriesID")
    private String seriesId;

    OmdbEpisode() {
    }

    public Integer getYear() {
        return year;
    }

    public Integer getCurrentSeason() {
        return currentSeason;
    }

    @Override
    public int getCurrentEpisode() {
        return currentEpisode;
    }

    public String getSeriesId() {
        return seriesId;
    }
}
