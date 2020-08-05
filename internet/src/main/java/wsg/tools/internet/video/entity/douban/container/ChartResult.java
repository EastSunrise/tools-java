package wsg.tools.internet.video.entity.douban.container;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.function.TitleSupplier;
import wsg.tools.internet.video.entity.douban.pojo.SimpleSubject;

/**
 * Result of chart of subjects.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Setter
@Getter
public class ChartResult extends PageResult<SimpleSubject> implements TitleSupplier {
    private String title;
}