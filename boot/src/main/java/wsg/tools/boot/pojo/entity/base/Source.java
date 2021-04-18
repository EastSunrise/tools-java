package wsg.tools.boot.pojo.entity.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.repository.Repository;
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

    public Source() {
    }

    public Source(@Nonnull String domain, int subtype) {
        this.domain = domain;
        this.subtype = subtype;
    }

    @Nonnull
    @Contract("_, _, _, _ -> new")
    public static Source of(@Nonnull String domain, int subtype, long rid,
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

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Integer getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(long rid) {
        this.rid = rid;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Source source = (Source) o;
        return Objects.equals(domain, source.domain) && Objects
            .equals(subtype, source.subtype) && Objects.equals(rid, source.rid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(domain, subtype, rid);
    }
}
