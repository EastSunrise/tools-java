package wsg.tools.internet.info.adult;

import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Adult videos on the {@link LaymanCatSite}.
 *
 * @author Kingen
 * @since 2021/2/28
 */
@Getter
public class LaymanCatItem implements Identifier<String>, UpdateDatetimeSupplier, NextSupplier<String> {

    private final String id;
    private final AdultEntry entry;
    private final String author;
    private final LocalDateTime published;
    private final LocalDateTime updated;
    private final String description;
    private final String next;

    LaymanCatItem(String id, AdultEntry entry, String author, LocalDateTime published, LocalDateTime updated, String next) {
        this(id, entry, author, published, updated, null, next);
    }

    LaymanCatItem(String id, AdultEntry entry, String author, LocalDateTime published, LocalDateTime updated, String description, String next) {
        this.id = AssertUtils.requireNotBlank(id);
        this.entry = Objects.requireNonNull(entry);
        this.author = AssertUtils.requireNotBlank(author);
        this.published = Objects.requireNonNull(published);
        this.updated = Objects.requireNonNull(updated);
        this.description = description;
        this.next = next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return updated;
    }

    @Override
    public String next() {
        return next;
    }
}
