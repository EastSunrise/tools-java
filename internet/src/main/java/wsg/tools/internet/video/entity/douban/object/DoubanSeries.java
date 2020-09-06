package wsg.tools.internet.video.entity.douban.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;

/**
 * TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class DoubanSeries extends BaseDoubanSubject {

    private Integer episodesCount;
}