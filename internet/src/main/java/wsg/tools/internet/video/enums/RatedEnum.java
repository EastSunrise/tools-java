package wsg.tools.internet.video.enums;

import wsg.tools.common.jackson.intf.AkaPredicate;

/**
 * Rate of movies on IMDb.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public enum RatedEnum implements AkaPredicate<String> {
    /**
     * Levels of rate
     */
    NOT_RATED("Not Rated"),
    RATED("Rated"),
    G("G"),
    TV_G("TV-G"),
    PG("PG"),
    TV_PG("TV-PG"),
    TV_Y7("TV-Y7"),
    PG_13("PG-13"),
    TV_14("TV-14"),
    TV_MA("TV-MA"),
    NC_17("NC-17"),
    R("R"),
    KT("KT"),
    PASSED("Passed"),
    X("X");

    private final String text;

    RatedEnum(String text) {
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        if (NOT_RATED.equals(this)) {
            return text.equalsIgnoreCase(other) || "UNRATED".equalsIgnoreCase(other);
        }
        return text.equalsIgnoreCase(other);
    }
}
