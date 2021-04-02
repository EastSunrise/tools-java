package wsg.tools.internet.movie.online;

import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * The status of a series.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public enum SeriesStatus implements TitleSupplier {
    /**
     * to be continued
     */
    AIRING("连载中"),
    /**
     * finished
     */
    CONCLUDED("已完结"),
    /**
     * not released
     */
    PREPARING("未开播");

    private final String title;

    SeriesStatus(String title) {
        this.title = title;
    }

    @Nonnull
    @Override
    public String toString() {
        return name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getTitle() {
        return title;
    }
}
