package wsg.tools.internet.movie.resource;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.view.SubtypeSupplier;
import wsg.tools.internet.movie.common.YearSupplier;

/**
 * Vod items of Grape site, which include downloadable and playable resources.
 *
 * @author Kingen
 * @see GrapeSite#findVodItem(String)
 * @since 2021/2/3
 */
public class GrapeVodItem extends BasicItem
    implements SubtypeSupplier<GrapeVodType>, GrapeVodIndex, YearSupplier {

    private final String path;
    private final String title;
    private final GrapeVodType type;
    private final LocalDateTime addTime;
    private String state;
    private Integer year;

    GrapeVodItem(@Nonnull String path, String title, GrapeVodType type, LocalDateTime addTime) {
        this.path = path;
        this.title = title;
        this.type = type;
        this.addTime = addTime;
    }

    @Override
    public GrapeVodType getSubtype() {
        return type;
    }

    @Override
    public String getAsPath() {
        return path;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public LocalDateTime getUpdate() {
        return addTime;
    }

    @Override
    public String getState() {
        return state;
    }

    void setState(String state) {
        this.state = state;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }
}
