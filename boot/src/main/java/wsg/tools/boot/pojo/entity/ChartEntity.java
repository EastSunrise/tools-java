package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import wsg.tools.boot.pojo.base.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity for movie chart.
 *
 * @author Kingen
 * @since 2020/7/27
 */
@Getter
@Setter
@Entity
@Table(name = "video_chart")
public class ChartEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String title;
    private Integer rank;
    private Integer delta;
    private LocalDate updateDate;
    @UpdateTimestamp
    private LocalDateTime gmtModified;
    private Integer deleted;
}
