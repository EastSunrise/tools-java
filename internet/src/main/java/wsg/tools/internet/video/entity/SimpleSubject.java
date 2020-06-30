package wsg.tools.internet.video.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.RecordEnum;

import java.time.LocalDate;

/**
 * Object of a simple subject.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Setter
@Getter
public class SimpleSubject {

    private Long id;
    private RecordEnum record;
    private LocalDate tagDate;
    private String title;
}
