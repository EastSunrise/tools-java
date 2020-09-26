package wsg.tools.boot.pojo.base;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;

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

    @UpdateTimestamp
    @Column(nullable = false, length = 6)
    private LocalDateTime gmtModified;
}
