package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbEpisodeView;
import wsg.tools.internet.video.site.imdb.ImdbEpisode;

/**
 * Projects {@link ImdbEpisode} to {@link ImdbEpisodeView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class ImdbEpisodeAdapter extends ImdbTitleAdapter<ImdbEpisode> implements ImdbEpisodeView {

    ImdbEpisodeAdapter(ImdbEpisode imdbEpisode) {
        super(imdbEpisode);
    }

    @Override
    public Integer getYear() {
        return t.getYear();
    }

    @Override
    public String getSeriesId() {
        return t.getSeriesId();
    }
}
