package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.UpdateTimestamp;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * Subject entity.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Getter
@Setter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "video_subject")
public class SubjectEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long dbId;
    private String imdbId;
    private VideoTypeEnum type;

    private String title;
    private String text;
    private String originalTitle;
    private List<String> textAka;
    private List<String> titleAka;
    private List<LanguageEnum> languages;
    private List<Duration> durations;
    private Year year;
    @UpdateTimestamp
    private LocalDateTime gmtModified;

    private Integer seasonsCount;

    private Integer currentSeason;
    private Integer episodesCount;
    private Long seriesId;

}
