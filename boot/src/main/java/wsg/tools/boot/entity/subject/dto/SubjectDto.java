package wsg.tools.boot.entity.subject.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.entity.subject.enums.ArchivedEnum;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.internet.video.enums.Language;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

/**
 * Object of subject.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Getter
@Setter
@TableName("video_subject")
public class SubjectDto {
    @TableId
    private Long id;
    private String imdbId;

    private String title;
    private String alt;
    private StatusEnum status;
    private LocalDate tagDate;
    private String originalTitle;
    private List<String> aka;
    private SubtypeEnum subtype;
    private Set<Language> languages;
    private Set<Duration> durations;
    private Year year;
    private ArchivedEnum archived;
    private String location;
    private Integer currentSeason;
    private Integer episodesCount;
    private Integer seasonsCount;
    @TableField(update = "now()")
    private LocalDateTime gmtModified;


}
