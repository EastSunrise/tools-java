package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbSeriesView;
import wsg.tools.internet.video.site.imdb.OmdbSeries;

import java.util.List;

/**
 * Projects {@link OmdbSeries} to {@link ImdbSeriesView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbSeriesAdapter extends OmdbTitleAdapter<OmdbSeries> implements ImdbSeriesView {

    private final List<String[]> episodes;

    OmdbSeriesAdapter(OmdbSeries omdbSeries, List<String[]> episodes) {
        super(omdbSeries);
        this.episodes = episodes;
    }

    @Override
    public Integer getSeasonsCount() {
        return t.getTotalSeasons();
    }

    @Override
    public List<String[]> getEpisodes() {
        return episodes;
    }

    @Override
    public Integer getYear() {
        return null;
    }
}
