package wsg.tools.boot.dao.api.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.boot.dao.api.ImdbView;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.imdb.ImdbTitle;

/**
 * Projects {@link ImdbTitle} to {@link ImdbView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
abstract class AbstractImdbTitleAdapter<T extends ImdbTitle> implements ImdbView {

    private final T t;

    AbstractImdbTitleAdapter(T t) {
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
    public List<Language> getLanguages() {
        return t.getLanguages();
    }

    @Override
    public LocalDate getRelease() {
        return t.getRelease();
    }

    @Override
    public List<Duration> getDurations() {
        Set<Duration> durations = new HashSet<>();
        CollectionUtils.addIgnoreNull(durations, t.getDuration());
        List<Duration> runtimes = t.getRuntimes();
        if (null != runtimes) {
            durations.addAll(runtimes);
        }
        return durations.isEmpty() ? null
            : durations.stream().sorted().collect(Collectors.toList());
    }

    T getT() {
        return t;
    }
}
