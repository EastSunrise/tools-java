package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

/**
 * @author Kingen
 * @since 2021/5/21
 */
class EpisodeIndex implements OmdbEpisodeIndex {

    @JsonProperty("imdbID")
    private String id;
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Episode")
    private int currentEpisode;
    @JsonProperty("Released")
    private LocalDate release;
    @JsonProperty("imdbRating")
    private Double imdbRating;

    EpisodeIndex() {
    }

    @Override
    public String getImdbId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public int getCurrentEpisode() {
        return currentEpisode;
    }

    @Override
    public LocalDate getRelease() {
        return release;
    }

    @Override
    public Double getImdbRating() {
        return imdbRating;
    }
}
