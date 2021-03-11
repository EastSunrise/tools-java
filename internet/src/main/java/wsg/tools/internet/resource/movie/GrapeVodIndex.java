package wsg.tools.internet.resource.movie;

import java.time.LocalDate;
import lombok.Getter;
import wsg.tools.internet.resource.common.StateSupplier;
import wsg.tools.internet.resource.common.UpdateDateSupplier;

/**
 * An index pointing to a {@link GrapeVodItem} in the {@link GrapeSite}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
@Getter
public class GrapeVodIndex implements UpdateDateSupplier, StateSupplier {

    private final String path;
    private final String title;
    private final LocalDate updateTime;
    private final String state;

    GrapeVodIndex(String path, String title, LocalDate updateTime,
        String state) {
        this.path = path;
        this.title = title;
        this.updateTime = updateTime;
        this.state = state;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public LocalDate lastUpdate() {
        return updateTime;
    }
}
