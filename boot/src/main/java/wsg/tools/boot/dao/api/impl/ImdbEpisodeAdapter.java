package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.ImdbEpisodeView;
import wsg.tools.internet.movie.imdb.ImdbEpisode;

/**
 * Projects {@link ImdbEpisode} to {@link ImdbEpisodeView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class ImdbEpisodeAdapter extends AbstractImdbTitleAdapter<ImdbEpisode> implements ImdbEpisodeView {

    ImdbEpisodeAdapter(ImdbEpisode imdbEpisode) {
        super(imdbEpisode);
    }

    @Override
    public Integer getYear() {
        return getT().getYear();
    }

    @Override
    public String getSeriesId() {
        return getT().getSeriesId();
    }
}
