package wsg.tools.internet.video.enums;

import wsg.tools.common.function.AkaPredicate;

/**
 * Types of subjects from IMDb.
 *
 * @author Kingen
 * @since 2020/7/18
 */
public enum ImdbTypeEnum implements AkaPredicate<String> {
    /**
     * movie/series/episode
     */
    MOVIE, SERIES, EPISODE;

    @Override
    public boolean alsoKnownAs(String other) {
        return name().equalsIgnoreCase(other);
    }
}
