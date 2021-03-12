package wsg.tools.internet.resource.movie;

import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.resource.common.StateSupplier;
import wsg.tools.internet.resource.common.SubtypeSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * Vod items of Grape site, which include downloadable and playable resources.
 *
 * @author Kingen
 * @since 2021/2/3
 */
public class GrapeVodItem extends BasicItem
    implements SubtypeSupplier<GrapeVodType>, YearSupplier, UpdateDatetimeSupplier, StateSupplier {

    private final String path;
    private final String url;
    private final GrapeVodType type;
    private final LocalDateTime addTime;
    private String state;
    private Integer year;

    GrapeVodItem(@Nonnull String path, @Nonnull String url, GrapeVodType type,
        LocalDateTime addTime) {
        this.path = path;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    @Override
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
    public GrapeVodType getSubtype() {
        return type;
    }
}
