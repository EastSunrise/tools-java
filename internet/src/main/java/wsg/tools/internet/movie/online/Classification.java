package wsg.tools.internet.movie.online;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.util.function.AliasSupplier;

/**
 * The classifications of movies.
 *
 * @author Kingen
 * @since 2021/5/30
 */
public enum Classification implements AliasSupplier {

    MOVIE("电影"),
    SERIES("电视剧"),
    DOCUMENTARY("纪录片");

    private final String displayName;

    Classification(String displayName) {
        this.displayName = displayName;
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    @Override
    public String[] getAlias() {
        return new String[]{displayName};
    }
}
