package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import wsg.tools.common.lang.AssertUtils;

/**
 * A collection of adult entries.
 *
 * @author Kingen
 * @see MidnightSite#findCollection(int)
 * @since 2021/2/22
 */
public class MidnightCollection extends BaseMidnightItem {

    /**
     * Pairs of serialNumber-cover of adult entries, one of the two elements may be null.
     */
    private final List<Pair<String, URL>> works;

    MidnightCollection(int id, String title, LocalDateTime addTime,
        List<Pair<String, URL>> works) {
        super(id, title, addTime);
        AssertUtils.requireNotEmpty(works, "works of the collection");
        this.works = works;
    }

    public List<Pair<String, URL>> getWorks() {
        return works;
    }
}
