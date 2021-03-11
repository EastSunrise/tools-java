package wsg.tools.internet.info.adult.wiki;

import javax.annotation.Nonnull;
import lombok.Getter;
import wsg.tools.internet.info.adult.common.AdultEntry;

/**
 * An adult entry of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class WikiAdultEntry {

    private final AdultEntry entry;
    private final WikiSimpleCelebrity celebrity;

    WikiAdultEntry(@Nonnull AdultEntry entry, WikiSimpleCelebrity celebrity) {
        this.entry = entry;
        this.celebrity = celebrity;
    }
}
