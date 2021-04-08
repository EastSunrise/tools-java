package wsg.tools.internet.movie.resource;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Objects;
import wsg.tools.internet.base.view.NextSupplier;
import wsg.tools.internet.base.view.UpdateDatetimeSupplier;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
public class XlmItem extends BaseIdentifiedItem<XlmColumn>
    implements UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final LocalDateTime releaseTime;
    private URL cover;
    private Integer next;

    XlmItem(XlmColumn subtype, URL source, int id, String title, LocalDateTime releaseTime) {
        super(subtype, source, id, title);
        this.releaseTime = Objects.requireNonNull(releaseTime, "the release time of an item");
    }

    @Override
    public LocalDateTime getUpdate() {
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
