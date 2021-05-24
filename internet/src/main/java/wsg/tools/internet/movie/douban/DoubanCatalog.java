package wsg.tools.internet.movie.douban;

import java.util.Locale;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * Topics on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2020/6/29
 */
public enum DoubanCatalog implements IntCodeSupplier, PathSupplier {

    /**
     * @see <a href="https://book.douban.com/">Books</a>
     */
    BOOK(1001, "author", "authors"),
    /**
     * @see <a href="https://movie.douban.com/">Movies</a>
     */
    MOVIE(1002, "celebrity", "celebrities"),
    /**
     * @see <a href="https://music.douban.com/">Music</a>
     */
    MUSIC(1003, "musician", "musicians");

    private final int code;
    private final String person;
    private final String personPlurality;

    DoubanCatalog(int code, String person, String personPlurality) {
        this.code = code;
        this.person = person;
        this.personPlurality = personPlurality;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Nonnull
    @Override
    public String getAsPath() {
        return name().toLowerCase(Locale.ROOT);
    }

    String getPerson() {
        return person;
    }

    String getPersonPlurality() {
        return personPlurality;
    }
}
