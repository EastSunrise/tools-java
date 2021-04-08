package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;

/**
 * A base item in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class BaseMidnightItem implements MidnightIndex {

    private final int id;
    private final String title;
    private final LocalDateTime addTime;
    private String[] keywords;

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
}
