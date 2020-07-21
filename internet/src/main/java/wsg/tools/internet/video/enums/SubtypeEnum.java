package wsg.tools.internet.video.enums;

import wsg.tools.common.jackson.intf.AkaPredicate;

/**
 * Subtype of subjects.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public enum SubtypeEnum implements AkaPredicate<String> {
    /**
     * Movie
     */
    MOVIE("movie"),
    TV("tv"),
    ;

    private final String[] aka;

    SubtypeEnum(String... aka) {
        this.aka = aka;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        for (String s : aka) {
            if (s.equalsIgnoreCase(other)) {
                return true;
            }
        }
        return false;
    }
}
