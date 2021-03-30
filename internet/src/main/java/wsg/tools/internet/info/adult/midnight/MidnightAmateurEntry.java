package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;
import wsg.tools.internet.info.adult.entry.AmateurSupplier;

/**
 * An item with an amateur adult entry.
 *
 * @author Kingen
 * @since 2021/3/28
 */
public class MidnightAmateurEntry extends BaseMidnightItem implements AmateurSupplier {

    private final AmateurAdultEntry entry;

    MidnightAmateurEntry(int id, String title, LocalDateTime release, AmateurAdultEntry entry) {
        super(id, title, release);
        this.entry = Objects.requireNonNull(entry, "the amateur entry");
    }

    @Nonnull
    @Override
    public AmateurAdultEntry getAmateurEntry() {
        return entry;
    }
}
