package wsg.tools.internet.movie.resource;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.SubtypeSupplier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.movie.common.YearSupplier;

/**
 * Vod items of Grape site, which include downloadable and playable resources.
 *
 * @author Kingen
 * @since 2021/2/3
 */
public class GrapeVodItem extends BasicItem
    implements SubtypeSupplier, YearSupplier, UpdateDatetimeSupplier {

    private final String path;
    private final GrapeVodType type;
    private final LocalDateTime addTime;
    private String state;
    private Integer year;

    GrapeVodItem(@Nonnull String path, GrapeVodType type, LocalDateTime addTime) {
        this.path = path;
        this.type = type;
        this.addTime = addTime;
    }

    public String getPath() {
        return path;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    public String getState() {
        return state;
    }

    void setState(String state) {
        this.state = state;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return addTime;
    }

    @Override
    public int getSubtype() {
        return type.getId();
    }
}
