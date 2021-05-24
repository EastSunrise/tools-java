package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import wsg.tools.internet.movie.common.RangeYear;

/**
 * A TV series retrieved from OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbSeries extends AbstractOmdbMovie {

    @JsonProperty("Year")
    private RangeYear year;
    @JsonProperty("totalSeasons")
    private Integer totalSeasons;

    OmdbSeries() {
    }

    public RangeYear getYear() {
        return year;
    }

    public Integer getTotalSeasons() {
        return totalSeasons;
    }
}
