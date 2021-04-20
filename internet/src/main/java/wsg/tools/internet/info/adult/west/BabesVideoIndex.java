package wsg.tools.internet.info.adult.west;

import java.time.Duration;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.info.adult.common.VideoQuality;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * An index pointing to a video on {@link BabesTubeSite}.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @see BabesPageResult
 * @since 2021/3/16
 */
public class BabesVideoIndex implements IntIdentifier, PathSupplier, TitledAdultEntry,
    DurationSupplier {

    private final int id;
    private final String titlePath;
    private final String title;
    private final Duration duration;
    private final double rating;
    private final int views;
    private VideoQuality quality;

    BabesVideoIndex(int id, String titlePath, String title, Duration duration, double rating,
        int views) {
        this.id = id;
        this.titlePath = titlePath;
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.views = views;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getAsPath() {
        return id + "/" + titlePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public int getViews() {
        return views;
    }

    public VideoQuality getQuality() {
        return quality;
    }

    void setQuality(VideoQuality quality) {
        this.quality = quality;
    }
}
