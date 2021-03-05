package wsg.tools.internet.movie.common.enums;

import wsg.tools.common.util.function.AkaPredicate;

/**
 * Subtype of subjects on {@link wsg.tools.internet.movie.douban.DoubanSite}.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public enum DoubanSubtype implements AkaPredicate<String> {
    /**
     * Movie/TV
     */
    MOVIE, TV,
    ;

    @Override
    public boolean alsoKnownAs(String other) {
        return name().equalsIgnoreCase(other);
    }
}
