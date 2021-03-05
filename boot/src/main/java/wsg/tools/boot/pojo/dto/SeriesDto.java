package wsg.tools.boot.pojo.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;

/**
 * Subject of series.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SeriesDto extends BaseDto {

    private static final long serialVersionUID = 8342913829890750191L;

    private SeriesEntity series;
    private List<SeasonDto> seasons;
    private int unarchived;
}
