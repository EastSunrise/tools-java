package wsg.tools.boot.pojo.entity.base;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import wsg.tools.internet.base.repository.Repository;

/**
 * Failures when importing from {@link Repository}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Entity
@Table(
    name = "failure",
    uniqueConstraints = @UniqueConstraint(name = "uni_failure_source", columnNames = {
        "domain", "subtype", "rid"
    })
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

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }
}
