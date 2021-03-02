package wsg.tools.internet.resource.site;

import wsg.tools.internet.base.NextSupplier;
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
public class XlmItem extends IdentifiedItem implements TypeSupplier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime releaseTime;
    private final XlmType type;
    private Integer next;

    XlmItem(int id, @Nonnull String url, LocalDateTime releaseTime, XlmType type) {
        super(id, url);
        this.releaseTime = releaseTime;
        this.type = type;
    }

    @Override
    public VideoType getType() {
        return type.getVideoType();
    }

    @Override
    public LocalDateTime lastUpdate() {
        return releaseTime;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public Integer next() {
        return next;
    }
}
