package wsg.tools.internet.movie.douban.api.container;

import java.util.List;
import lombok.Getter;
import wsg.tools.internet.common.TitleSupplier;
import wsg.tools.internet.movie.douban.api.pojo.SimpleSubject;

/**
 * Result of subjects with ranked info.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
public class RankedResult implements TitleSupplier {

    private String title;
    private List<RankedSubject> subjects;

    @Getter
    public static class RankedSubject {

        private int rank;
        private int delta;
        private SimpleSubject subject;
    }
}
