package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import wsg.tools.internet.base.view.SiblingSupplier;

/**
 * A base item in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class BaseMidnightItem implements MidnightIndex, SiblingSupplier<Integer> {

    private final int id;
    private final String title;
    private final LocalDateTime addTime;
    private String[] keywords;
    private Integer previousId;
    private Integer nextId;

    BaseMidnightItem(int id, String title, LocalDateTime addTime) {
        this.id = id;
        this.title = title;
        this.addTime = addTime;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
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
