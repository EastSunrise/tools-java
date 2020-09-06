package wsg.tools.internet.video.entity.omdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.info.YearInfo;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;

/**
 * OMDb TV series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class OmdbSeries extends BaseOmdbTitle {

    private YearInfo year;
    @JsonProperty("TotalSeasons")
    private Integer seasonsCount;
}
