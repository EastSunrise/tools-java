package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;

/**
 * An actress in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/2/22
 */
@Getter
public class MidnightActress extends BaseMidnightItem {

    /**
     * Map of code-cover of adult videos, the cover may be null.
     */
    private Map<String, String> works;

    MidnightActress(int id, String title, LocalDateTime release) {
        super(id, title, release);
    }

    void setWorks(Map<String, String> works) {
        this.works = Collections.unmodifiableMap(works);
    }
}
