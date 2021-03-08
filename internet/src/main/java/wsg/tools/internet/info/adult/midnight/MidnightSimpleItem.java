package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;

/**
 * A simple item which points to a {@link BaseMidnightItem} in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
@Getter
public class MidnightSimpleItem {

    private final int id;
    private final String title;
    /**
     * Not precise
     */
    private final LocalDateTime simpleRelease;

    MidnightSimpleItem(int id, String title, LocalDateTime simpleRelease) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.simpleRelease = Objects.requireNonNull(simpleRelease);
    }
}
