package wsg.tools.boot.pojo.entity.base;

import java.io.Serializable;
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
     * The identifier of the record in the repository
     */
    @Column(nullable = false, length = 15)
    private String rid;

    public Source(String repository, String rid) {
        this.repository = repository;
        this.rid = rid;
    }

    public Source() {
    }
}
