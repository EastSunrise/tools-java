package wsg.tools.internet.info.adult;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.Identifier;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;
import wsg.tools.internet.info.adult.entry.AmateurSupplier;

/**
 * An item on the {@link AmateurCatSite}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class AmateurCatItem
    implements Identifier<String>, UpdateDatetimeSupplier, AmateurSupplier, NextSupplier<String> {

    private final String id;
    private String author;
    private LocalDateTime published;
    private LocalDateTime updated;
    private AmateurAdultEntry entry;
    private String next;

    AmateurCatItem(String id) {
        this.id = id;
    }

    void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }

    void setEntry(AmateurAdultEntry entry) {
        this.entry = entry;
    }

    void setNext(String next) {
        this.next = next;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    void setPublished(LocalDateTime published) {
        this.published = published;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updated;
    }

    @Override
    public String nextId() {
        return next;
    }

    @Nonnull
    @Override
    public AmateurAdultEntry getAmateurEntry() {
        return entry;
    }
}
