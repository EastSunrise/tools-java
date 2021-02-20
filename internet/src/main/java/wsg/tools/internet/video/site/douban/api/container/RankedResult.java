package wsg.tools.internet.video.site.douban.api.container;

import lombok.Getter;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.video.site.douban.api.pojo.SimpleSubject;

import java.util.List;

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
