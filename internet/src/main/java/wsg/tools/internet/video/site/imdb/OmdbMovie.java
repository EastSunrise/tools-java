package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import wsg.tools.common.lang.Money;

import java.time.LocalDate;
import java.time.Year;

/**
 * OMDb movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbMovie extends OmdbTitle {

    private Year year;

    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate dvd;
    private Money boxOffice;
    private String production;
    private String website;

}
