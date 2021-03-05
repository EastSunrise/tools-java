package wsg.tools.internet.movie.imdb;

/**
 * Supply id of IMDb.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@FunctionalInterface
public interface ImdbIdentifier {

    /**
     * Return id of IMDb.
     *
     * @return id
     */
    String getImdbId();
}
