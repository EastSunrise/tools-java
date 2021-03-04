package wsg.tools.internet.info.adult;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.info.adult.common.Mosaic;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * The entry of an adult video that only has one performer.
 *
 * @author Kingen
 * @since 2021/3/3
 */
public class SingleAdultEntry extends AdultEntry {

    private final String performer;

    protected SingleAdultEntry(String code, String cover, String performer, String title, Mosaic mosaic, Duration duration, LocalDate release,
                               String director, String producer, String distributor, String series, List<String> tags) {
        super(code, cover, title, mosaic, duration, release, director, producer, distributor, series, tags);
        this.performer = AssertUtils.requireNotBlank(performer);
    }

    public String getPerformer() {
        return performer;
    }
}
