package wsg.tools.boot.dao.api.impl;

import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.intf.ImdbSeriesView;
import wsg.tools.internet.movie.imdb.ImdbSeries;

/**
 * Projects {@link ImdbSeries} to {@link ImdbSeriesView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class ImdbSeriesAdapter extends AbstractImdbTitleAdapter<ImdbSeries> implements ImdbSeriesView {

    ImdbSeriesAdapter(ImdbSeries imdbSeries) {
        super(imdbSeries);
    }

    @Nonnull
    @Override
    public Integer getSeasonsCount() {
        return Math.max(getT().getEpisodes().size(), 1);
    }

    @Override
    public List<String[]> getEpisodes() {
        return getT().getEpisodes();
    }

    @Override
    public Integer getYear() {
        return getT().getRangeYear().getStart();
    }
}
