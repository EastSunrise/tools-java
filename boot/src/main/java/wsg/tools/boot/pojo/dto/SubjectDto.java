package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseDto;
import wsg.tools.boot.pojo.enums.ArchivedEnum;
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
import wsg.tools.internet.video.enums.LanguageEnum;

import java.time.Duration;
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
    private VideoTypeEnum type;

    private String title;
    private String text;
    private String originalTitle;
    private List<String> textAka;
    private List<String> titleAka;
    private List<LanguageEnum> languages;
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
