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
    private FailureReason reason;

    @Column(nullable = false)
    private Boolean solved;

    @Column
    private String arg1;

    @Column
    private String arg2;

    @Column
    private String arg3;

    public Failure(Source source, FailureReason reason, String arg1) {
        this(source, reason, arg1, null);
    }

    public Failure(Source source, FailureReason reason, String arg1, String arg2) {
        this(source, reason, arg1, arg2, null);
    }

    public Failure(Source source, FailureReason reason, String arg1, String arg2, String arg3) {
        this.source = source;
        this.reason = reason;
        this.solved = false;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    protected Failure() {
    }

    public Source getSource() {
        return source;
    }

    public FailureReason getReason() {
        return reason;
    }

    public Boolean getSolved() {
        return solved;
    }

    public String getArg1() {
        return arg1;
    }

    public String getArg2() {
        return arg2;
    }

    public String getArg3() {
        return arg3;
    }
}
