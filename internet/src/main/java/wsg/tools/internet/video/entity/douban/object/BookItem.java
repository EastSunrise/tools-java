package wsg.tools.internet.video.entity.douban.object;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.BaseSuggestItem;

import java.time.Year;

/**
 * Item of suggested book.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class BookItem extends BaseSuggestItem {

    private long id;
    private String title;
    private String authorName;
    private Year year;
    private String pic;
    private String url;
}