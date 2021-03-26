package wsg.tools.internet.resource.movie;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.resource.common.CoverSupplier;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
public class XlmItem extends IdentifiedItem<XlmColumn>
    implements UpdateDatetimeSupplier, NextSupplier<Integer>, CoverSupplier {

    private final LocalDateTime releaseTime;
    private URL cover;
    private Integer next;

    XlmItem(int id, @Nonnull String url, LocalDateTime releaseTime, XlmColumn column) {
        super(id, url, column);
        this.releaseTime = Objects.requireNonNull(releaseTime, "the release time of an item");
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

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }
}
