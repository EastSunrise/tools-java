package wsg.tools.internet.info.adult.wiki;

import javax.annotation.Nonnull;
import lombok.Getter;
import wsg.tools.internet.info.adult.FormalAdultEntry;

/**
 * An adult entry of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class WikiAdultEntry {

    private final FormalAdultEntry entry;
    private final WikiSimpleCelebrity celebrity;

    WikiAdultEntry(@Nonnull FormalAdultEntry entry, WikiSimpleCelebrity celebrity) {
        this.entry = entry;
        this.celebrity = celebrity;
    }
}
