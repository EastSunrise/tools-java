package wsg.tools.internet.video.enums;

import wsg.tools.common.function.AkaPredicate;

/**
 * Subtype of subjects.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public enum SubtypeEnum implements AkaPredicate<String> {
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
