package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import lombok.Getter;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;

/**
 * Adult videos on the {@link LaymanCatSite}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Getter
public class LaymanCatItem implements Identifier<String>, UpdateDatetimeSupplier,
    NextSupplier<String> {

    private final String id;
    private String author;
    private LocalDateTime published;
    private LocalDateTime updated;
    private AdultEntry entry;
    private String description;
    private String next;

    LaymanCatItem(String id) {
        this.id = id;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    void setPublished(LocalDateTime published) {
        this.published = published;
    }

    void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    void setEntry(AdultEntry entry) {
        this.entry = entry;
    }

    void setDescription(String description) {
        this.description = description;
    }

    void setNext(String next) {
        this.next = next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updated;
    }

    @Override
    public String nextId() {
        return next;
    }
}
