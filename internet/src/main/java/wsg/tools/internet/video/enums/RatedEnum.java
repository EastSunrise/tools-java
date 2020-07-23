package wsg.tools.internet.video.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.jackson.intf.AkaPredicate;

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
    G,
    PG,
    PG_13("PG-13"),
    R,
    NC_17("NC-17"),

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
        return name().equalsIgnoreCase(other) || ArrayUtils.contains(aka, other);
    }
}
