package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;

/**
 * Subject object for movie and season.
 *
 * @author Kingen
 * @since 2020/11/22
 */
@Getter
@Setter
public class SubjectDto extends BaseDto implements DoubanIdentifier {

    private Long id;
    private String title;
    private Long dbId;
    private String durations;
    private boolean archived;
}
