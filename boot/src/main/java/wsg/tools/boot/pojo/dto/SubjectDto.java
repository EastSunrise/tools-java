package wsg.tools.boot.pojo.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.internet.movie.douban.DoubanIdentifier;

/**
 * Subject object for movie and season.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SubjectDto extends BaseDto implements DoubanIdentifier {

    private static final long serialVersionUID = 1667441730744897649L;

    private Long id;
    private String zhTitle;
    private Integer year;
    private Long dbId;
    private String durations;
    private VideoStatus status;
    private LocalDateTime gmtModified;
}
