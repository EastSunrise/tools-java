package wsg.tools.boot.dao.api.support;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import wsg.tools.boot.dao.api.ImdbRepo;
import wsg.tools.boot.dao.api.ImdbView;
import wsg.tools.boot.pojo.error.UnknownTypeException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.movie.omdb.AbstractOmdbMovie;
import wsg.tools.internet.movie.omdb.OmdbApi;
import wsg.tools.internet.movie.omdb.OmdbEpisode;
import wsg.tools.internet.movie.omdb.OmdbEpisodeIndex;
import wsg.tools.internet.movie.omdb.OmdbMovie;
import wsg.tools.internet.movie.omdb.OmdbReq;
import wsg.tools.internet.movie.omdb.OmdbSeason;
import wsg.tools.internet.movie.omdb.OmdbSeries;

/**
 * The proxy of the {@link OmdbApi}, as an implementation of {@link ImdbRepo}.
 *
 * @author Kingen
 * @since 2021/5/21
 */
class OmdbProxy implements ImdbRepo {

    private final OmdbApi omdbApi;

    OmdbProxy(OmdbApi omdbApi) {
        this.omdbApi = omdbApi;
    }

    @Override
    public ImdbView findSubjectById(String imdbId)
        throws NotFoundException, OtherResponseException {
        AbstractOmdbMovie movie = omdbApi.findMovie(OmdbReq.ofId(imdbId), null);
        if (movie instanceof OmdbMovie) {
            return new OmdbMovieAdapter((OmdbMovie) movie);
        }
        if (movie instanceof OmdbEpisode) {
            return new OmdbEpisodeAdapter((OmdbEpisode) movie);
        }
        if (movie instanceof OmdbSeries) {
            OmdbSeries series = (OmdbSeries) movie;
            int totalSeasons = series.getTotalSeasons();
            List<String[]> allEpisodes = new ArrayList<>();
            for (int i = 1; i <= totalSeasons; i++) {
                OmdbSeason season = null;
                try {
                    season = omdbApi.findSeason(OmdbReq.ofId(imdbId), i);
                } catch (NotFoundException e) {
                    throw new NotFoundException("OMDb season " + i + " of " + imdbId);
                }
                List<OmdbEpisodeIndex> episodes = season.getEpisodes();
                int maxEpisode = episodes.stream().mapToInt(OmdbEpisodeIndex::getCurrentEpisode)
                    .max().orElseThrow();
                String[] episodeIds = new String[maxEpisode + 1];
                episodes.forEach(e -> episodeIds[e.getCurrentEpisode()] = e.getImdbId());
                allEpisodes.add(episodeIds);
            }
            return new OmdbSeriesAdapter(series, allEpisodes);
        }
        throw new UnknownTypeException(movie.getClass());
    }

    @Override
    public void close() throws IOException {
        if (omdbApi instanceof Closeable) {
            ((Closeable) omdbApi).close();
        }
    }
}
