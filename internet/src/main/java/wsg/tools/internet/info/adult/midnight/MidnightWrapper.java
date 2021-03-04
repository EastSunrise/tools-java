package wsg.tools.internet.info.adult.midnight;

import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.resource.common.UpdateDatetimeSupplier;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * A wrapper of an item in the {@link MidnightSite}, containing a specific
 * kind of {@link #content}.
 *
 * @param <T> type of the content wrapped within the item
 * @author Kingen
 * @since 2021/3/2
 */
@Getter
public class MidnightWrapper<T extends TitleSupplier> implements IntIdentifier, UpdateDatetimeSupplier, NextSupplier<Integer> {

    private final int id;
    private final LocalDateTime release;
    private final T content;
    private String[] keywords;
    private Integer next;

    MidnightWrapper(int id, LocalDateTime release, T content) {
        this.id = id;
        this.release = Objects.requireNonNull(release);
        this.content = Objects.requireNonNull(content);
    }

    void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public Integer next() {
        return next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return release;
    }
}
