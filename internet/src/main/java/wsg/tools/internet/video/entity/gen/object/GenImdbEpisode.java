package wsg.tools.internet.video.entity.gen.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.gen.base.BaseGenImdbTitle;

/**
 * IMDb TV episode from PT Gen.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Setter
@Getter
public class GenImdbEpisode extends BaseGenImdbTitle {

    private Integer popularity;
}