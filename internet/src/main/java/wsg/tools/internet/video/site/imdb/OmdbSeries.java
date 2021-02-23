package wsg.tools.internet.video.site.imdb;

import lombok.Getter;
import wsg.tools.internet.video.common.RangeYear;

import javax.annotation.Nullable;

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
