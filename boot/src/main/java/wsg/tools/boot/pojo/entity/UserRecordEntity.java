package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.movie.common.enums.MarkEnum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

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
public class UserRecordEntity extends IdentityEntity {

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long subjectId;

    @Column(nullable = false)
    private MarkEnum mark;

    @Column(nullable = false)
    private LocalDate markDate;
}
