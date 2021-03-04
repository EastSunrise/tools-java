package wsg.tools.internet.resource.movie;

import wsg.tools.internet.resource.common.*;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;

/**
 * Vod items of Grape site, which include downloadable and playable resources.
 *
 * @author Kingen
 * @since 2021/2/3
 */
public class GrapeVodItem extends BasicItem implements VideoTypeSupplier, YearSupplier, UpdateDatetimeSupplier, StateSupplier {

    private final String url;
    private final GrapeVodGenre genre;
    private final LocalDateTime addTime;
    private String state;
    private Integer year;

    GrapeVodItem(@Nonnull String url, GrapeVodGenre genre, LocalDateTime addTime) {
        this.url = url;
        this.genre = genre;
        this.addTime = addTime;
    }

    @Override
    public VideoType getType() {
        return genre.getType();
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
