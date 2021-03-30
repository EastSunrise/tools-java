package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Objects;
import wsg.tools.internet.base.IntIdentifier;

/**
 * An index pointing to a {@link BaseMidnightItem} in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightIndex implements IntIdentifier {

    private final int id;
    private final String title;
    private final LocalDateTime release;

    MidnightIndex(int id, String title, LocalDateTime release) {
        this.id = id;
        this.title = Objects.requireNonNull(title);
        this.release = Objects.requireNonNull(release);
    }

    @Override
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getRelease() {
        return release;
    }
}
