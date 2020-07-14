package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.StatusEnum;
import wsg.tools.boot.pojo.enums.SubtypeEnum;
import wsg.tools.internet.video.enums.Language;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDate;
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
@Table(name = "video_subject")
public class SubjectEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long dbId;
    private String imdbId;

    private String title;
    private String text;
    private StatusEnum status;
    private LocalDate tagDate;
    private String originalTitle;
    private List<String> aka;
    private SubtypeEnum subtype;
    private List<Language> languages;
    private List<Duration> durations;
    private Year year;
    private ArchivedEnum archived;
    private String location;
    private Integer currentSeason;
    private Integer episodesCount;
    private Integer seasonsCount;
    private LocalDateTime gmtModified;
}
