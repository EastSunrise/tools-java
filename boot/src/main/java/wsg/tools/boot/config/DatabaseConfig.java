package wsg.tools.boot.config;

import org.springframework.context.annotation.Configuration;

/**
 * Configuration related to database.
 *
 * @author Kingen
 * @since 2021/2/17
 */
@Configuration
public class DatabaseConfig {

    public static final long MOVIE_ID_START = 10_000_000;
    public static final long SERIES_ID_START = 30_000_000;
    public static final long SEASON_ID_START = 40_000_000;
    public static final long EPISODE_ID_START = 60_000_000;

    public boolean isMovie(long id) {
        return id >= MOVIE_ID_START && id < SEASON_ID_START;
    }

    public boolean isSeries(long id) {
        return id >= SERIES_ID_START && id < SEASON_ID_START;
    }

    public boolean isSeason(long id) {
        return id >= SEASON_ID_START && id < EPISODE_ID_START;
    }

    public boolean isEpisode(long id) {
        return id >= EPISODE_ID_START;
    }
}
