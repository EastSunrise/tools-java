package wsg.tools.internet.movie.common.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * The source of a rating.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public enum RatingSource implements TextSupplier {
    /**
     * @see <a href="https://www.imdb.com/">IMDb</a>
     */
    IMDB("Internet Movie Database"),
    /**
     * @see <a href="https://www.rottentomatoes.com/">Rotten Tomatoes</a>
     */
    ROTTEN_TOMATOES("Rotten Tomatoes"),
    /**
     * @see <a href="https://www.metacritic.com/">IMDb</a>
     */
    META_CRITIC("Metacritic");

    private final String text;

    RatingSource(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
