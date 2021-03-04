package wsg.tools.internet.info.adult;

import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.info.adult.common.Mosaic;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * The entry of an adult video that contains more than one performers.
 *
 * @author Kingen
 * @since 2021/3/3
 */
public class MultiAdultEntry extends AdultEntry {

    private final List<String> performers;

    protected MultiAdultEntry(String code, String cover, List<String> performers, String title, Mosaic mosaic, Duration duration, LocalDate release,
                              String director, String producer, String distributor, String series, List<String> tags) {
        super(code, cover, title, mosaic, duration, release, director, producer, distributor, series, tags);
        this.performers = AssertUtils.require(performers, CollectionUtils::isNotEmpty, "The performers may not be null.");
    }

    public List<String> getPerformers() {
        return performers;
    }
}
