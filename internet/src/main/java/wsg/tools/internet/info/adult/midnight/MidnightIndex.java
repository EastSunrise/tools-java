package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;

/**
 * An index pointing to a {@link BaseMidnightItem} in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
@Getter
public class MidnightIndex implements IntIdentifier {

    private final int id;
    private final String title;
    /**
     * Not precise
     */
    private final LocalDateTime release;

    MidnightIndex(int id, String title, LocalDateTime release) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.release = Objects.requireNonNull(release);
    }
}
