package wsg.tools.internet.video.entity.douban;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Rating of douban.
 *
 * @author Kingen
 * @since 2020/7/18
 */
@Setter
@Getter
public class Rating {
    private Integer max;
    private Double average;
    private Map<Integer, Integer> details;
    private Integer stars;
    private Integer min;
    private Integer value;
}