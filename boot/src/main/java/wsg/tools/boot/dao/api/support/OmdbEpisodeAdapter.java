package wsg.tools.boot.dao.api.support;

import wsg.tools.boot.dao.api.ImdbEpisodeView;
import wsg.tools.internet.movie.omdb.OmdbEpisode;

/**
 * Projects {@link OmdbEpisode} to {@link ImdbEpisodeView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbEpisodeAdapter extends AbstractOmdbMovieAdapter<OmdbEpisode> implements ImdbEpisodeView {

    OmdbEpisodeAdapter(OmdbEpisode omdbEpisode) {
        super(omdbEpisode);
    }

    @Override
    public String getSeriesId() {
        return getT().getSeriesId();
    }

    @Override
    public Integer getYear() {
        return getT().getYear();
    }
}
