package wsg.tools.internet.video.enums;

import wsg.tools.common.function.AkaPredicate;

import java.util.Arrays;

/**
 * Rate of movies on IMDb.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public enum RatedEnum implements AkaPredicate<String> {
    /**
     * Rate of movies
     */
    NOT_RATED("Not Rated", "Unrated"),
    RATED,
    G("Approved"),
    PG,
    PG_13("PG-13"),
    R,
    NC_17("NC-17"),
    M("X", "P"),

    /**
     * Rate of TV
     */
    TV_Y("TV-Y"),
    TV_Y7("TV-Y7"),
    TV_Y7_FV("TV-Y7-FV"),
    TV_G("TV-G"),
    TV_PG("TV-PG"),
    TV_14("TV-14"),
    TV_MA("TV-MA"),
    PASSED,
    ;

    private final String[] aka;

    RatedEnum(String... aka) {
        this.aka = aka;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return name().equalsIgnoreCase(other) || Arrays.stream(aka).anyMatch(s -> s.equalsIgnoreCase(other));
    }
}
