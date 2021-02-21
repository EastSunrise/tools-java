package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbMovieView;
import wsg.tools.internet.video.site.imdb.ImdbMovie;

/**
 * Projects {@link ImdbMovie} to {@link ImdbMovieView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class ImdbMovieAdapter extends ImdbTitleAdapter<ImdbMovie> implements ImdbMovieView {

    ImdbMovieAdapter(ImdbMovie imdbMovie) {
        super(imdbMovie);
    }

    @Override
    public Integer getYear() {
        return t.getYear();
    }
}
