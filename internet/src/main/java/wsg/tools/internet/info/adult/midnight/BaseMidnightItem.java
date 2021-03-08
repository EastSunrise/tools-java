package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;

/**
 * A base item in the {@link MidnightSite}.
 *
 * @author Kingen
 * @see MidnightActress
 * @see MidnightAlbum
 * @see MidnightEntry
 * @since 2021/3/2
 */
@Getter
public abstract class BaseMidnightItem extends MidnightSimpleItem
    implements IntIdentifier, TitleSupplier, UpdateDatetimeSupplier {

    private final LocalDateTime release;
    private String[] keywords;

    BaseMidnightItem(int id, String title, LocalDateTime release) {
        super(id, title, release);
        this.release = release;
    }

    void setKeywords(String[] keywords) {
        this.keywords = keywords.clone();
    }

    @Override
    public LocalDateTime lastUpdate() {
        return release;
    }
}
