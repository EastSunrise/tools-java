package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import wsg.tools.internet.info.adult.common.AdultEntry;

/**
 * An item with an adult entry.
 *
 * @author Kingen
 * @since 2021/3/10
 */
public class MidnightAdultEntry extends BaseMidnightEntry {

    @Getter
    private final AdultEntry entry;

    MidnightAdultEntry(int id, String title, LocalDateTime release, AdultEntry entry) {
        super(id, title, release);
        this.entry = Objects.requireNonNull(entry, "entry of Midnight");
    }
}
