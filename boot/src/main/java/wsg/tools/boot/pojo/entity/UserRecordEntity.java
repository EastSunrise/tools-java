package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.internet.video.enums.MarkEnum;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity of marking relationship between users and subjects.
 *
 * @author Kingen
 * @since 2020/8/1
 */
@Setter
@Getter
@Entity
@Table(name = "user_record")
public class UserRecordEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long subjectId;
    private MarkEnum mark;
    private LocalDate markDate;
    @UpdateTimestamp
    private LocalDateTime gmtModified;
}
