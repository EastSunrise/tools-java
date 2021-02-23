package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Entity of series of adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@Getter
@Setter
@Entity
@Table(name = "adult_series")
public class AdultSeriesEntity extends BaseEntity {

    @Id
    private Integer id;
    @Column(nullable = false)
    private String title;
    @Column(length = 15)
    private String keyword;
    @Column(nullable = false)
    private LocalDateTime addTime;
}
