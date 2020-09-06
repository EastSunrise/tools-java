package wsg.tools.internet.video.entity.douban.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.BaseSuggestItem;

/**
 * Item of suggested author.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class AuthorItem extends BaseSuggestItem {

    private long id;
    private String title;
    private String enName;
    private String pic;
    private String url;
}
