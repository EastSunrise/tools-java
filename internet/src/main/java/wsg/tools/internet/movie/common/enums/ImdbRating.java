package wsg.tools.internet.movie.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.internet.enums.Region;

/**
 * Ratings of movies on IMDb.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public enum ImdbRating implements AkaPredicate<String> {
    ;

    private final Region region;
    private final String certificate;

    ImdbRating(Region region, String certificate) {
        this.region = region;
        this.certificate = certificate;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return (region.getText() + ":" + certificate).equals(other)
            || (region == Region.US && certificate.equalsIgnoreCase(other));
    }
}
