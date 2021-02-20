package wsg.tools.internet.video.site.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;

import java.util.Map;

/**
 * Rating of douban.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Getter
public class Rating {

    private Double max;
    private Double average;
    private Map<Integer, Integer> details;
    @JsonAlias("numRaters")
    private Integer stars;
    private Double min;
    private Double value;
}
