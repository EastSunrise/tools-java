package wsg.tools.boot.pojo.dto;

import java.time.Duration;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;

/**
 * Subject of a season.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SeasonDto extends SubjectDto {

    private static final long serialVersionUID = 3218502545058189095L;

    private Integer currentSeason;
    private Integer episodesCount;

    public static SeasonDto fromEntity(SeasonEntity entity) {
        SeasonDto seasonDto = new SeasonDto();
        seasonDto.setId(entity.getId());
        seasonDto.setZhTitle(entity.getZhTitle());
        seasonDto.setYear(entity.getYear());
        seasonDto.setDbId(entity.getDbId());
        seasonDto
            .setDurations(
                entity.getDurations().stream().map(Duration::toMinutes).map(String::valueOf)
                    .collect(Collectors.joining("/")));
        seasonDto.setCurrentSeason(entity.getCurrentSeason());
        seasonDto.setEpisodesCount(entity.getEpisodesCount());
        return seasonDto;
    }
}
