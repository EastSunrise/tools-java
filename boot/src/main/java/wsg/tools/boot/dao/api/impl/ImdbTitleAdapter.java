package wsg.tools.boot.dao.api.impl;

import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.boot.dao.api.intf.ImdbView;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.imdb.ImdbTitle;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Projects {@link ImdbTitle} to {@link ImdbView}.
 *
 * @author Kingen
 * @since 2021/2/21
 */
abstract class ImdbTitleAdapter<T extends ImdbTitle> implements ImdbView {

    final T t;

    ImdbTitleAdapter(T t) {
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
    public List<LanguageEnum> getLanguages() {
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
        if (runtimes != null) {
            durations.addAll(runtimes);
        }
        return durations.isEmpty() ? null : durations.stream().sorted().collect(Collectors.toList());
    }
}
