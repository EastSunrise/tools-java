package wsg.tools.boot.pojo.entity.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.common.UpdateTemporalSupplier;

/**
 * The source of an entity from a {@link Repository}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Embeddable
public class Source implements Serializable {

    /**
     * Default subtype for the repository which has only one subtype.
     */
    public static final int DEFAULT_SUBTYPE = 0;
    private static final long serialVersionUID = 38168257706310693L;

    /**
     * The domain of the {@link Repository}
     */
    @Column(nullable = false, length = 31)
    private String domain;

    /**
     * The subtype that the entity belongs to in the repository
     */
    @Column(nullable = false)
    private Integer subtype;

    /**
     * The identifier of the entity in the repository
     */
    @Column(nullable = false)
    private Long rid;

    /**
     * The timestamp of the entity in the repository, like release time or update time.
     */
    @Column(length = 0)
    private LocalDateTime timestamp;

    private Source(String domain, Integer subtype, Long rid, LocalDateTime timestamp) {
        this.domain = domain;
        this.subtype = subtype;
        this.rid = rid;
        this.timestamp = timestamp;
    }

    protected Source() {
    }

    /**
     * Returns an instance of {@link Source} representing a record of the given subtype of the given
     * repository.
     */
    @Nonnull
    @Contract(value = "_, _, _ -> new", pure = true)
    public static Source record(@Nonnull String domain, int subtype, @Nonnull IntIdentifier item) {
        LocalDateTime timestamp = null;
        if (item instanceof UpdateDatetimeSupplier) {
            timestamp = ((UpdateDatetimeSupplier) item).getUpdate();
        } else if (item instanceof UpdateDateSupplier) {
            timestamp = ((UpdateDateSupplier) item).getUpdate().atTime(LocalTime.MIDNIGHT);
        }
        return new Source(domain, subtype, (long) item.getId(), timestamp);
    }

    @Nonnull
    @Contract("_, _, _, _ -> new")
    public static Source record(@Nonnull String domain, int subtype, long rid,
        UpdateTemporalSupplier<?> supplier) {
        LocalDateTime timestamp = null;
        if (supplier != null) {
            if (supplier instanceof UpdateDatetimeSupplier) {
                timestamp = ((UpdateDatetimeSupplier) supplier).getUpdate();
            } else if (supplier instanceof UpdateDateSupplier) {
                timestamp = ((UpdateDateSupplier) supplier).getUpdate().atTime(LocalTime.MIDNIGHT);
            }
        }
        return new Source(domain, subtype, rid, timestamp);
    }

    public String getDomain() {
        return domain;
    }

    public Integer getSubtype() {
        return subtype;
    }

    public Long getRid() {
        return rid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
