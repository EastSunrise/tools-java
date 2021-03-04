package wsg.tools.internet.info.adult.mr;

import lombok.Getter;
import wsg.tools.internet.info.adult.AdultEntry;

import javax.annotation.Nonnull;

/**
 * An adult entry of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class CelebrityAdultEntry {

    private final AdultEntry entry;
    private SimpleCelebrity celebrity;

    CelebrityAdultEntry(@Nonnull AdultEntry entry) {
        this.entry = entry;
    }

    CelebrityAdultEntry(@Nonnull AdultEntry entry, @Nonnull SimpleCelebrity celebrity) {
        this.entry = entry;
        this.celebrity = celebrity;
    }
}
