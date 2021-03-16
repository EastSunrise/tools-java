package wsg.tools.boot.pojo.entity.base;

import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Getter;
import wsg.tools.internet.base.intf.Repository;

/**
 * The source of a record from a {@link Repository}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Getter
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
    @Column(nullable = false, length = 15)
    private String domain;

    /**
     * The subtype that the record belongs to in the repository
     */
    @Column(nullable = false)
    private Integer subtype;

    /**
     * The identifier of the record in the repository
     */
    @Column(nullable = false)
    private Long rid;

    private Source(String domain, Integer subtype, Long rid) {
        this.domain = domain;
        this.subtype = subtype;
        this.rid = rid;
    }

    public Source() {
    }

    /**
     * Returns an instance of {@link Source} representing the default subtype of the given
     * repository, usually the whole repository which only has one subtype.
     */
    public static Source repo(@Nonnull String domain) {
        return new Source(domain, null, null);
    }

    /**
     * Returns an instance of {@link Source} representing a subtype of the given repository.
     */
    public static Source subtype(@Nonnull String domain, int subtype) {
        return new Source(domain, subtype, null);
    }

    /**
     * Returns an instance of {@link Source} representing a record of the given subtype of the given
     * repository.
     */
    public static Source record(@Nonnull String domain, int subtype, long rid) {
        return new Source(domain, subtype, rid);
    }
}
