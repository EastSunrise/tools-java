package wsg.tools.internet.resource.site;

import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
public class XlmItem extends IdentifiedItem implements TypeSupplier, UpdateDatetimeSupplier {

    private final LocalDateTime releaseTime;
    private final VideoType type;

    XlmItem(int id, @Nonnull String url, LocalDateTime releaseTime, VideoType type) {
        super(id, url);
        this.releaseTime = releaseTime;
        this.type = type;
    }

    @Override
    public VideoType getType() {
        return type;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return releaseTime;
    }
}
