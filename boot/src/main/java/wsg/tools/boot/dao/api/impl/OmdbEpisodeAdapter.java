package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbEpisodeView;
import wsg.tools.internet.video.site.imdb.OmdbEpisode;

/**
 * Projects {@link OmdbEpisode} to {@link ImdbEpisodeView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
class OmdbEpisodeAdapter extends OmdbTitleAdapter<OmdbEpisode> implements ImdbEpisodeView {

    OmdbEpisodeAdapter(OmdbEpisode omdbEpisode) {
        super(omdbEpisode);
    }

    @Override
    public String getSeriesId() {
        return t.getSeriesId();
    }
}
