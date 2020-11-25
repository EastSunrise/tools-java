package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;

import java.util.List;

/**
 * Subject of series.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SeriesDto {

    private SeriesEntity series;
    private List<SeasonDto> seasons;
    private boolean archived;
}
