package wsg.tools.internet.movie.imdb;

import java.util.Locale;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * Types of movies to search on OMDb.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public enum MovieType implements PathSupplier {
    /**
     * movie/series/episode
     */
    MOVIE, SERIES, EPISODE;

    @Override
    public String getAsPath() {
        return name().toLowerCase(Locale.ROOT);
    }
}
