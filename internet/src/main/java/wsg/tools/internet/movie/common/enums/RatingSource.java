package wsg.tools.internet.movie.common.enums;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.util.function.AliasSupplier;

/**
 * The source of a rating.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public enum RatingSource implements AliasSupplier {
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

    private final String name;

    RatingSource(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    @Override
    public String[] getAlias() {
        return new String[]{name};
    }
}
