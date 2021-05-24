package wsg.tools.internet.movie.douban.api.container;

import wsg.tools.internet.common.TitleSupplier;
import wsg.tools.internet.movie.douban.api.pojo.SimpleSubject;

/**
 * Result of chart of subjects.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public class ChartResult extends PageResult<SimpleSubject> implements TitleSupplier {

    private String title;

    @Override
    public String getTitle() {
        return title;
    }
}
