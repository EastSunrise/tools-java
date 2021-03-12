package wsg.tools.internet.resource.movie;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
public class XlmItem extends IdentifiedItem<XlmColumn>
    implements UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime releaseTime;
    private Integer next;

    XlmItem(int id, @Nonnull String url, LocalDateTime releaseTime, XlmColumn column) {
        super(id, url, column);
        this.releaseTime = releaseTime;
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
