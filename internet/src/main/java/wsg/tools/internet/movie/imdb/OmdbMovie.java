package wsg.tools.internet.movie.imdb;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Getter;
import wsg.tools.common.lang.Money;

/**
 * OMDb movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class OmdbMovie extends OmdbTitle {

    private Integer year;
    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate dvd;
    private Money boxOffice;
    private String production;
    private String website;

}
