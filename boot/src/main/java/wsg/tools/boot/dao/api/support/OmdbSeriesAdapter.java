package wsg.tools.boot.dao.api.support;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.ImdbSeriesView;
import wsg.tools.internet.movie.omdb.OmdbSeries;

/**
 * Projects {@link OmdbSeries} to {@link ImdbSeriesView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbSeriesAdapter extends AbstractOmdbMovieAdapter<OmdbSeries> implements ImdbSeriesView {

    private final List<String[]> episodes;

    OmdbSeriesAdapter(OmdbSeries omdbSeries, List<String[]> episodes) {
        super(omdbSeries);
        this.episodes = Collections.unmodifiableList(episodes);
    }

    @Nonnull
    @Override
    public Integer getSeasonsCount() {
        return Objects.requireNonNullElse(getT().getTotalSeasons(), 1);
    }

    @Override
    public List<String[]> getEpisodes() {
        return episodes;
    }

    @Override
    public Integer getYear() {
        return getT().getYear().getStart();
    }
}
