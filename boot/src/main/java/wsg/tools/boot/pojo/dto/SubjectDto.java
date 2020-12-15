package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.VideoStatus;
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
    private Integer year;
    private Long dbId;
    private String durations;
    private VideoStatus status;

    public boolean isArchived() {
        return status == VideoStatus.ARCHIVED || status == VideoStatus.COMING;
    }
}
