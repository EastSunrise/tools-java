package wsg.tools.internet.movie.resource;

import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * The types of items on {@link EightyMovieSite}.
 *
 * @author Kingen
 * @since 2021/6/15
 */
public enum EightyType implements PathSupplier {

    MOVIE, TV, CARTOON, VARIETY;

    @Nonnull
    @Override
    public String getAsPath() {
        return name().toLowerCase(Locale.ROOT);
    }
}
