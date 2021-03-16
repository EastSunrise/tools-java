package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import wsg.tools.common.lang.AssertUtils;

/**
 * A collection of adult entries.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Getter
public class MidnightCollection extends BaseMidnightItem {

    /**
     * Pairs of code-cover of adult entries, one of the two elements may be null.
     */
    private final List<Pair<String, String>> works;

    MidnightCollection(int id, String title, LocalDateTime release,
        List<Pair<String, String>> works) {
        super(id, title, release);
        AssertUtils.requireNotEmpty(works, "works of the collection");
        this.works = works;
    }
}
