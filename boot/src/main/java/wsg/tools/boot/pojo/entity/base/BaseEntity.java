package wsg.tools.boot.pojo.entity.base;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Base class for entities queried from database.
 *
 * @author Kingen
 * @since 2020/7/12
 */
@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1923844612718234234L;
    @UpdateTimestamp
    @Column(nullable = false, length = 6)
    private LocalDateTime gmtModified;
}
