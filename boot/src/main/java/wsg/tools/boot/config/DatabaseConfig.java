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

    private static final long MOVIE_ID_START = 10_000_000L;
    private static final long SERIES_ID_START = 30_000_000L;
    private static final long SEASON_ID_START = 40_000_000L;
    private static final long EPISODE_ID_START = 60_000_000L;

    public static boolean isMovie(long id) {
        return id >= MOVIE_ID_START && id < SEASON_ID_START;
    }

    public static boolean isSeries(long id) {
        return id >= SERIES_ID_START && id < SEASON_ID_START;
    }

    public static boolean isSeason(long id) {
        return id >= SEASON_ID_START && id < EPISODE_ID_START;
    }

    public static boolean isEpisode(long id) {
        return id >= EPISODE_ID_START;
    }
}
