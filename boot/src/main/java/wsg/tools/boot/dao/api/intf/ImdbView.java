package wsg.tools.boot.dao.api.intf;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.common.YearSupplier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * View of a subject from IMDb.
 *
 * @author Kingen
 * @since 2021/2/21
 */
public interface ImdbView extends ImdbIdentifier, YearSupplier {

    /**
     * Obtains title in English
     *
     * @return title
     */
    String getEnTitle();

    /**
     * Obtains the release date of the subject
     *
     * @return the date
     */
    LocalDate getRelease();

    /**
     * Obtains languages
     *
     * @return list of languages
     */
    List<Language> getLanguages();

    /**
     * Obtains durations of the subject
     *
     * @return list of durations
     */
    List<Duration> getDurations();
}
