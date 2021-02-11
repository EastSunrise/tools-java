package wsg.tools.internet.video.site.douban;

import javax.annotation.Nullable;

/**
 * TV Series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class DoubanSeries extends BaseDoubanSubject {

    private long[] seasons;
    private Integer currentSeason;
    private Integer episodesCount;

    DoubanSeries() {
    }

    @Nullable
    public long[] getSeasons() {
        return seasons;
    }

    void setSeasons(long[] seasons) {
        this.seasons = seasons;
    }

    @Nullable
    public Integer getCurrentSeason() {
        return currentSeason;
    }

    void setCurrentSeason(int currentSeason) {
        this.currentSeason = currentSeason;
    }

    @Nullable
    public Integer getEpisodesCount() {
        return episodesCount;
    }

    void setEpisodesCount(int episodesCount) {
        this.episodesCount = episodesCount;
    }
}