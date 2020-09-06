package wsg.tools.internet.video.entity.imdb.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;

import java.time.Duration;
import java.util.List;

/**
 * IMDb Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class ImdbMovie extends BaseImdbTitle {

    private Duration duration;

    private ImdbVideoObject trailer;

    @JsonProperty("director")
    private List<ImdbPerson> directors;
}
