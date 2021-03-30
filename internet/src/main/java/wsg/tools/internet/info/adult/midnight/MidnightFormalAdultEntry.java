package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import wsg.tools.internet.info.adult.entry.FormalAdultEntry;

/**
 * An item with a formal adult entry.
 *
 * @author Kingen
 * @since 2021/3/10
 */
public class MidnightFormalAdultEntry extends BaseMidnightItem {

    private final FormalAdultEntry entry;

    MidnightFormalAdultEntry(int id, String title, LocalDateTime release, FormalAdultEntry entry) {
        super(id, title, release);
        this.entry = Objects.requireNonNull(entry, "the formal entry");
    }

    public FormalAdultEntry getEntry() {
        return entry;
    }
}
