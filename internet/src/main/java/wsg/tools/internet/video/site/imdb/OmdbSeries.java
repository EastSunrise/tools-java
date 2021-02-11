package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import wsg.tools.internet.video.site.imdb.pojo.info.YearInfo;

/**
 * OMDb TV series.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbSeries extends OmdbTitle {

    private YearInfo year;
    @JsonProperty("TotalSeasons")
    private Integer seasonsCount;
}
