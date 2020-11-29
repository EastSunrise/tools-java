package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

/**
 * Condition of querying resources.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
@Setter
public class ResourceQueryDto extends BaseDto implements DoubanIdentifier, ImdbIdentifier {

    private String key;
    private Long dbId;
    private String imdbId;
}
