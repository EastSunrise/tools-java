package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbMovieView;
import wsg.tools.internet.video.site.imdb.OmdbMovie;

/**
 * Projects {@link OmdbMovie} to {@link ImdbMovieView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbMovieAdapter extends OmdbTitleAdapter<OmdbMovie> implements ImdbMovieView {

    OmdbMovieAdapter(OmdbMovie omdbMovie) {
        super(omdbMovie);
    }

    @Override
    public Integer getYear() {
        return t.getYear();
    }
}
