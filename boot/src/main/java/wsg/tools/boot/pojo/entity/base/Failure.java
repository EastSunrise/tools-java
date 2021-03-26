package wsg.tools.boot.pojo.entity.base;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.base.repository.Repository;

/**
 * Failures when importing from {@link Repository}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Getter
@Setter
@Entity
@Table(
    name = "failure",
    uniqueConstraints = @UniqueConstraint(
        name = "unique_failure_source",
        columnNames = {"domain", "rid"}
    )
)
public class Failure extends IdentityEntity {

    private static final long serialVersionUID = 6395141771348858128L;

    @Embedded
    private Source source;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false)
    private Boolean solved;

    public Failure(Source source, String reason) {
        this.source = source;
        this.reason = reason;
        this.solved = false;
    }

    public Failure() {

    }
}
