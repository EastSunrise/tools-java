package wsg.tools.internet.resource.movie;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.common.VideoTypeSupplier;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
public class XlmItem extends IdentifiedItem implements VideoTypeSupplier, UpdateDatetimeSupplier,
    NextSupplier<Integer> {

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
    public Integer nextId() {
        return next;
    }
}
