package wsg.tools.internet.resource.entity.rrys.info;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class Prevue {

    private Integer season;
    private Integer episode;
    private LocalDate playTime;
    private DayOfWeek week;
    private LocalDateTime t;
}
