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

    private static final long serialVersionUID = 38168257706310693L;

    /**
     * The {@link Repository} where the records are from, usually domain
     */
    @Column(nullable = false, length = 15)
    private String repository;

    /**
     * The subtype of the record in the repository
     */
    @Column(nullable = false)
    private Integer subtype;

    /**
     * The identifier of the record in the repository
     */
    @Column(nullable = false)
    private Long rid;

    private Source(String repository, int subtype, Long rid) {
        this.repository = repository;
        this.subtype = subtype;
        this.rid = rid;
    }

    public Source() {
    }

    /**
     * Returns an instance of {@link Source} representing the default subtype of the given
     * repository, usually the whole repository which only has one subtype.
     */
    public static Source repo(@Nonnull String repository) {
        return new Source(repository, 0, null);
    }

    /**
     * Returns an instance of {@link Source} representing a record of the default subtype of the
     * given repository.
     */
    public static Source record(@Nonnull String repository, @Nonnull Long rid) {
        return new Source(repository, 0, rid);
    }

    /**
     * Returns an instance of {@link Source} representing a subtype of the given repository.
     */
    public static Source subtype(@Nonnull String repository, int subtype) {
        return new Source(repository, subtype, null);
    }

    /**
     * Returns an instance of {@link Source} representing a record of the given subtype of the given
     * repository.
     */
    public static Source record(@Nonnull String repository, int subtype, @Nonnull Long rid) {
        return new Source(repository, subtype, rid);
    }
}
