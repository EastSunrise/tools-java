package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbSeriesView;
import wsg.tools.internet.movie.imdb.OmdbSeries;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * Projects {@link OmdbSeries} to {@link ImdbSeriesView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbSeriesAdapter extends AbstractOmdbTitleAdapter<OmdbSeries> implements ImdbSeriesView {

    private final List<String[]> episodes;

    OmdbSeriesAdapter(OmdbSeries omdbSeries, List<String[]> episodes) {
        super(omdbSeries);
        this.episodes = episodes;
    }

    @Nonnull
    @Override
    public Integer getSeasonsCount() {
        return Objects.requireNonNullElse(t.getTotalSeasons(), 1);
    }

    @Override
    public List<String[]> getEpisodes() {
        return episodes;
    }

    @Override
    public Integer getYear() {
        return t.getYear().getStart();
    }
}
