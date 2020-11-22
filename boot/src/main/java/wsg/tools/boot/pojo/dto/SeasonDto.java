package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Subject of a season.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SeasonDto extends SubjectDto {

    private Integer currentSeason;
}
