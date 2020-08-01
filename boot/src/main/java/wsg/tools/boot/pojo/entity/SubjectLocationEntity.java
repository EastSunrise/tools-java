package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.boot.pojo.enums.ArchivedEnum;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * Entity for location info of subjects.
 *
 * @author Kingen
 * @since 2020/8/2
 */
@Setter
@Getter
@Entity
public class SubjectLocationEntity extends BaseEntity {
    @Id
    private Long id;
    private ArchivedEnum archived;
    private String location;
    @UpdateTimestamp
    private LocalDateTime gmtModified;
}
