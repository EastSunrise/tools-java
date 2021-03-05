package wsg.tools.internet.movie.imdb;

import javax.annotation.Nullable;
import lombok.Getter;
import wsg.tools.internet.movie.common.RangeYear;

/**
 * OMDb TV series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbSeries extends OmdbTitle {

    private RangeYear year;
    private Integer totalSeasons;

    @Nullable
    public Integer getTotalSeasons() {
        return totalSeasons;
    }
}
