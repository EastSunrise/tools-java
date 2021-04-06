package wsg.tools.internet.movie.common.enums;

import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.PathSupplier;

/**
 * Enum of marking type on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum DoubanMark implements TitleSupplier, PathSupplier {
    /**
     * wish/do/collect
     */
    WISH("想看"),
    DO("在看"),
    COLLECT("看过");

    private final String title;

    DoubanMark(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public String getAsPath() {
        return name().toLowerCase(Locale.ROOT);
    }
}
