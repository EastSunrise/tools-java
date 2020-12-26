package wsg.tools.internet.resource.entity.rrys.jackson;

import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;

import java.util.Objects;

/**
 * Play statuses of series.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
public class SeriesPlayStatus implements PlayStatus {

    public static final SeriesPlayStatus SERIES_NOT_START = new SeriesPlayStatus(SeasonStatus.NOT_START);
    public static final SeriesPlayStatus SERIES_SERIALIZED = new SeriesPlayStatus(SeasonStatus.SERIALIZED);
    public static final SeriesPlayStatus SERIES_FINISHED = new SeriesPlayStatus(SeasonStatus.FINISHED);

    private final int currentSeason;
    private final SeasonStatus status;

    private SeriesPlayStatus(SeasonStatus status) {
        this.currentSeason = -1;
        this.status = Objects.requireNonNull(status);
    }

    SeriesPlayStatus(int currentSeason, SeasonStatus status) {
        this.currentSeason = AssertUtils.requireRange(currentSeason, 1, null);
        this.status = Objects.requireNonNull(status);
    }

    enum SeasonStatus {
        NOT_START, SERIALIZED, FINISHED
    }
}
