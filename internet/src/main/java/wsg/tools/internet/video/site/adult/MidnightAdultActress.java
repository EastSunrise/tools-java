package wsg.tools.internet.video.site.adult;

import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Items of {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Getter
public class MidnightAdultActress implements IntIdentifier, NextSupplier<Integer>, UpdateDatetimeSupplier {

    private final int id;
    private final String title;
    private final LocalDateTime release;
    private List<MidnightAdultVideo> works;
    private String keyword;
    private Integer next;

    MidnightAdultActress(int id, String title, LocalDateTime release) {
        this.id = id;
        this.title = title;
        this.release = release;
    }

    @Override
    public int getId() {
        return id;
    }

    void setWorks(List<MidnightAdultVideo> works) {
        this.works = works;
    }

    void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    void setNext(Integer next) {
        this.next = next;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return release;
    }

    @Override
    public Integer next() {
        return next;
    }
}
