package wsg.tools.internet.movie.douban;

import java.time.LocalDate;
import wsg.tools.internet.common.UpdateDateSupplier;

/**
 * A subject with marking information.
 *
 * @author Kingen
 * @see wsg.tools.internet.movie.common.enums.DoubanMark
 * @since 2021/5/20
 */
public class MarkedSubject extends BasicSubject implements UpdateDateSupplier {

    private final LocalDate markedDate;

    MarkedSubject(long id, DoubanCatalog catalog, String title, LocalDate markedDate) {
        super(id, catalog, title);
        this.markedDate = markedDate;
    }

    @Override
    public LocalDate getUpdate() {
        return markedDate;
    }
}
