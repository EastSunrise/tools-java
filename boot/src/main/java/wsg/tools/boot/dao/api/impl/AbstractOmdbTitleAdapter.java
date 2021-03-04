package wsg.tools.boot.dao.api.impl;

import wsg.tools.boot.dao.api.intf.ImdbView;
import wsg.tools.internet.movie.common.Runtime;
import wsg.tools.internet.movie.common.enums.LanguageEnum;
import wsg.tools.internet.movie.imdb.OmdbTitle;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * Projects {@link OmdbTitle} to {@link ImdbView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
abstract class AbstractOmdbTitleAdapter<T extends OmdbTitle> implements ImdbView {

    final T t;

    AbstractOmdbTitleAdapter(T t) {
        this.t = t;
    }

    @Override
    public String getEnTitle() {
        return t.getEnTitle();
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
    public List<LanguageEnum> getLanguages() {
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
}
