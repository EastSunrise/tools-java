package wsg.tools.internet.video.entity.douban.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;

import java.time.Duration;
import java.util.List;

/**
 * Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class DoubanMovie extends BaseDoubanSubject {

    private List<Duration> extDurations;
}