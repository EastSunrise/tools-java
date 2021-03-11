package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
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
     * Map of code-cover of adult videos, the cover may be null.
     */
    private final Map<String, String> works;

    MidnightCollection(int id, String title, LocalDateTime release, Map<String, String> works) {
        super(id, title, release);
        AssertUtils.requireNotEmpty(works, "works of the collection");
        this.works = Collections.unmodifiableMap(works);
    }
}
