package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbSeriesView;
import wsg.tools.internet.video.site.imdb.ImdbSeries;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Projects {@link ImdbSeries} to {@link ImdbSeriesView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class ImdbSeriesAdapter extends ImdbTitleAdapter<ImdbSeries> implements ImdbSeriesView {

    ImdbSeriesAdapter(ImdbSeries imdbSeries) {
        super(imdbSeries);
    }

    @Nonnull
    @Override
    public Integer getSeasonsCount() {
        return Math.max(t.getEpisodes().size(), 1);
    }

    @Override
    public List<String[]> getEpisodes() {
        return t.getEpisodes();
    }

    @Override
    public Integer getYear() {
        return t.getRangeYear().getStart();
    }
}
