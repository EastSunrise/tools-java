package wsg.tools.boot.pojo.entity.base;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Failures when importing from {@link wsg.tools.internet.base.intf.Repository}.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Getter
@Setter
@Entity
@Table
public class Failure extends IdentityEntity {

    private static final long serialVersionUID = 6395141771348858128L;

    @Embedded
    private Source source;

    @Column(nullable = false)
    private String reason;

    public Failure(Source source, String reason) {
        this.source = source;
        this.reason = reason;
    }

    public Failure() {

    }
}
