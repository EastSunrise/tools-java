package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import lombok.Getter;
import wsg.tools.internet.base.Identifier;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;
import wsg.tools.internet.info.adult.entry.AmateurSupplier;

/**
 * An item in the {@link LicencePlateSite}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
@Getter
public class LicencePlateItem
    implements Identifier<String>, UpdateDatetimeSupplier, NextSupplier<String>, AmateurSupplier {

    private final String id;
    private final LocalDateTime updateTime;
    private final AmateurAdultEntry entry;
    private final String nextId;
    private String intro;

    LicencePlateItem(String id, LocalDateTime updateTime, AmateurAdultEntry entry, String nextId) {
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

    public String getIntro() {
        return intro;
    }

    void setIntro(String intro) {
        this.intro = intro;
    }

    @Nonnull
    @Override
    public AmateurAdultEntry getAmateurEntry() {
        return entry;
    }
}
