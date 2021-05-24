package wsg.tools.boot.dao.api.support;

import wsg.tools.boot.dao.api.ImdbMovieView;
import wsg.tools.internet.movie.omdb.OmdbMovie;

/**
 * Projects {@link OmdbMovie} to {@link ImdbMovieView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbMovieAdapter extends AbstractOmdbMovieAdapter<OmdbMovie> implements ImdbMovieView {

    OmdbMovieAdapter(OmdbMovie omdbMovie) {
        super(omdbMovie);
    }

    @Override
    public Integer getYear() {
        return getT().getYear();
    }
}
