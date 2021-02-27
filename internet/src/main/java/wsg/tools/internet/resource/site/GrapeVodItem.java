package wsg.tools.internet.resource.site;

import wsg.tools.internet.resource.item.BaseItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.StateSupplier;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Vod items of Grape site, which include downloadable and playable resources.
 *
 * @author Kingen
 * @since 2021/2/3
 */
public class GrapeVodItem extends BaseItem implements TypeSupplier, YearSupplier, UpdateDatetimeSupplier, StateSupplier {

    private final String url;
    private final VideoType type;
    private final LocalDateTime addTime;
    private String state;
    private Integer year;

    GrapeVodItem(@Nonnull String url, VideoType type, LocalDateTime addTime) {
        this.url = url;
        this.type = type;
        this.addTime = addTime;
    }

    @Override
    public VideoType getType() {
        return type;
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
}
