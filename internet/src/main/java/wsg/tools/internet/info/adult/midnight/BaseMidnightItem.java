package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import wsg.tools.internet.base.view.SiblingSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;

/**
 * A base item in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class BaseMidnightItem extends MidnightIndex
    implements UpdateDatetimeSupplier, SiblingSupplier<Integer> {

    private final LocalDateTime addTime;
    private String[] keywords;
    private Integer previousId;
    private Integer nextId;

    BaseMidnightItem(int id, String title, LocalDateTime addTime) {
        super(id, title);
        this.addTime = addTime;
    }

    @Override
    public LocalDateTime getUpdate() {
        return addTime;
    }

    public String[] getKeywords() {
        return keywords;
    }

    void setKeywords(String[] keywords) {
        this.keywords = keywords;
    }

    @Override
    public Integer getNextId() {
        return nextId;
    }

    void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    @Override
    public Integer getPreviousId() {
        return previousId;
    }

    void setPreviousId(Integer previousId) {
        this.previousId = previousId;
    }
}
