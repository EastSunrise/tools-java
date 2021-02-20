package wsg.tools.internet.video.site.douban.api.container;

import lombok.Getter;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.video.site.douban.api.pojo.SimpleSubject;

/**
 * Result of chart of subjects.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
public class ChartResult extends PageResult<SimpleSubject> implements TitleSupplier {
    private String title;
}
