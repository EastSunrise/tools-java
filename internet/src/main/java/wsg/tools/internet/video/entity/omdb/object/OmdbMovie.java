package wsg.tools.internet.video.entity.omdb.object;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.lang.Money;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;

import java.time.LocalDate;
import java.time.Year;

/**
 * OMDb movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class OmdbMovie extends BaseOmdbTitle {

    private Year year;

    @JsonFormat(pattern = "dd MMM yyyy")
    private LocalDate dvd;
    private Money boxOffice;
    private String production;
    private String website;

}
