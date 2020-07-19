package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.internet.video.enums.Language;
import wsg.tools.internet.video.enums.MarkEnum;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

/**
 * Subject object.
 *
 * @author Kingen
 * @since 2020/7/12
 */
@Getter
@Setter
public class SubjectDto extends BaseDto {

    private Long id;
    private Long dbId;
    private String imdbId;
    private TypeEnum type;

    private String title;
    private String text;
    private MarkEnum mark;
    private LocalDate markDate;
    private String originalTitle;
    private List<String> textAka;
    private List<String> titleAka;
    private List<Language> languages;
    private List<Duration> durations;
    private Year year;
    private ArchivedEnum archived;
    private String location;
    private LocalDateTime gmtModified;

    private Integer seasonsCount;

    private Integer currentSeason;
    private Integer episodesCount;
    private Long seriesId;
}
