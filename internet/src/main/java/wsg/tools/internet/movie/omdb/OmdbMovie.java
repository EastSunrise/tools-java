package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import wsg.tools.common.lang.Money;

/**
 * A movie retrieved from OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbMovie extends AbstractOmdbMovie {

    @JsonProperty("Year")
    private Integer year;
    @JsonProperty("DVD")
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate dvd;
    @JsonProperty("BoxOffice")
    private Money boxOffice;
    @JsonProperty("Production")
    private String production;
    @JsonProperty("Website")
    private String website;

    OmdbMovie() {
    }

    public Integer getYear() {
        return year;
    }

    public LocalDate getDvd() {
        return dvd;
    }

    public Money getBoxOffice() {
        return boxOffice;
    }

    public String getProduction() {
        return production;
    }

    public String getWebsite() {
        return website;
    }
}
