package wsg.tools.internet.movie.common.enums;

import lombok.Getter;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Topics on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2020/6/29
 */
public enum DoubanCatalog implements IntCodeSupplier {
    /**
     * movie/book/music
     */
    BOOK(1001, DoubanCreator.AUTHOR),
    MOVIE(1002, DoubanCreator.CELEBRITY),
    MUSIC(1003, DoubanCreator.MUSICIAN);

    private final int code;
    @Getter
    private final DoubanCreator creator;

    DoubanCatalog(int code, DoubanCreator creator) {
        this.code = code;
        this.creator = creator;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
