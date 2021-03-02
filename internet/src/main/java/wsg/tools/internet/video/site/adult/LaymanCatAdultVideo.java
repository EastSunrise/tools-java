package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Adult videos on the {@link LaymanCatSite}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Getter
public class LaymanCatAdultVideo extends BaseAdultVideo implements UpdateDatetimeSupplier, NextSupplier<String> {

    private final String cover;
    private final LocalDateTime published;
    private final LocalDateTime updated;
    private final String author;

    private String title;
    private String description;
    private Duration duration;
    private Boolean released;
    private LocalDate post;
    private LocalDate release;
    private String series;
    private String distributor;
    private String producer;
    private List<String> tags;

    private String next;

    LaymanCatAdultVideo(String code, String cover, LocalDateTime published, LocalDateTime updated, String author) {
        super(code);
        this.cover = cover;
        this.published = published;
        this.updated = updated;
        this.author = author;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updated;
    }

    @Override
    public String next() {
        return next;
    }

    void setTitle(String title) {
        this.title = title;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    void setReleased(Boolean released) {
        this.released = released;
    }

    void setPost(LocalDate post) {
        this.post = post;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    void setSeries(String series) {
        this.series = series;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }

    void setNext(String next) {
        this.next = next;
    }
}
