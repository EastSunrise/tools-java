package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@Table(name = "video_series")
public class SeriesEntity extends SubjectEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 127)
    private String text;

    @Column(nullable = false)
    private Integer seasonsCount;
}
