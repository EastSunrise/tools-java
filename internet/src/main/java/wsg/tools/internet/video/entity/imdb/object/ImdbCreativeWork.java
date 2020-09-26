package wsg.tools.internet.video.entity.imdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import java.time.Year;
import java.util.List;

/**
 * IMDb creative works.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbCreativeWork extends BaseImdbTitle {

    private Year year;

    @JsonProperty("director")
    private List<ImdbPerson> directors;
}
