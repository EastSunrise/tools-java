package wsg.tools.boot.dao.api.support;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.boot.dao.api.ImdbView;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.omdb.AbstractOmdbMovie;

/**
 * Projects {@link AbstractOmdbMovie} to {@link ImdbView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
abstract class AbstractOmdbMovieAdapter<T extends AbstractOmdbMovie> implements ImdbView {

    private final T t;

    AbstractOmdbMovieAdapter(T t) {
        this.t = t;
    }

    @Override
    public String getEnTitle() {
        return t.getTitle();
    }

    @Override
    public String getImdbId() {
        return t.getImdbId();
    }

    @Override
    public LocalDate getRelease() {
        return t.getRelease();
    }

    @Override
    public List<Language> getLanguages() {
        return t.getLanguages();
    }

    @Override
    public List<Duration> getDurations() {
        Runtime runtime = t.getRuntime();
        if (runtime == null) {
            return null;
        }
        return List.of(runtime.getDuration());
    }

    T getT() {
        return t;
    }
}
