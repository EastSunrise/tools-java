package wsg.tools.internet.movie.common.enums;

import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * Enum of marking type on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum DoubanMark implements PathSupplier {
    /**
     * wish/do/collect
     */
    WISH, DO, COLLECT;

    @Nonnull
    @Override
    public String getAsPath() {
        return name().toLowerCase(Locale.ROOT);
    }
}
