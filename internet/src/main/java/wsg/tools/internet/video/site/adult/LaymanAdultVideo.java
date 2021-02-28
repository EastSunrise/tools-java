package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import org.jsoup.nodes.Element;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Adult videos on the {@link LaymanCatSite}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Getter
public class LaymanAdultVideo extends BasicAdultVideo implements UpdateDatetimeSupplier {

    private final LocalDateTime published;
    private final LocalDateTime updated;
    private final String author;

    private Map<String, List<Element>> content;
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
    private Map<String, String> info;

    private String previous;
    private String next;

    LaymanAdultVideo(String code, String cover, LocalDateTime published, LocalDateTime updated, String author) {
        super(code, cover);
        this.published = published;
        this.updated = updated;
        this.author = author;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updated;
    }

    public boolean hasNext() {
        return next != null;
    }

    void setContent(Map<String, List<Element>> content) {
        this.content = content;
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

    void setInfo(Map<String, String> info) {
        this.info = info;
    }

    void setPrevious(String previous) {
        this.previous = previous;
    }

    void setNext(String next) {
        this.next = next;
    }
}
