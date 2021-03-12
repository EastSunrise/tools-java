package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import lombok.Getter;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.common.AdultEntry;

/**
 * An item in the {@link LicencePlateSite}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
@Getter
public class LicencePlateItem
    implements Identifier<String>, UpdateDatetimeSupplier, NextSupplier<String> {

    private final String id;
    private final LocalDateTime updateTime;
    private final AdultEntry entry;
    private final String nextId;

    LicencePlateItem(String id, LocalDateTime updateTime, AdultEntry entry, String nextId) {
        this.id = id;
        this.updateTime = updateTime;
        this.entry = entry;
        this.nextId = nextId;
    }

    @Override
    public String nextId() {
        return nextId;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updateTime;
    }
}
