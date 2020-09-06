package wsg.tools.internet.video.entity.douban.object;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.BaseSuggestItem;

import java.time.Year;

/**
 * Item of suggested movie.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class MovieItem extends BaseSuggestItem {

    private long id;
    private String title;
    @JsonProperty("sub_title")
    private String originalTitle;
    private Year year;
    private String img;
    private String url;
    @JsonProperty("episode")
    private Integer episodesCount;
}
