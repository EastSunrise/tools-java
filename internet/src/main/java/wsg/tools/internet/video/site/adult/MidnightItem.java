package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.resource.item.intf.UpdateTimeSupplier;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Items of {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Getter
public class MidnightItem implements Identifier<Integer>, UpdateTimeSupplier<LocalDateTime> {

    private final int id;
    private final String title;
    private final List<AdultVideo> works;
    private final LocalDateTime release;
    private String keyword;

    MidnightItem(int id, String title, List<AdultVideo> works, LocalDateTime release) {
        this.id = id;
        this.title = title;
        this.works = works;
        this.release = release;
    }

    @Override
    public Integer getId() {
        return id;
    }

    void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return null;
    }
}
