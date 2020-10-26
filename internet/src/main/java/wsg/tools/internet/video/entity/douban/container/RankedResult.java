package wsg.tools.internet.video.entity.douban.container;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.video.entity.douban.pojo.SimpleSubject;

import java.util.List;

/**
 * Result of subjects with ranked info.
 *
 * @author Kingen
 * @since 2020/7/26
 */
@Getter
@Setter
public class RankedResult implements TitleSupplier {
    private String title;
    private List<RankedSubject> subjects;

    @Setter
    @Getter
    public static class RankedSubject {
        private int rank;
        private int delta;
        private SimpleSubject subject;
    }
}
