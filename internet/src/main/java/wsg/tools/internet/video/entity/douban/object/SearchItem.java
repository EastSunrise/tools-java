package wsg.tools.internet.video.entity.douban.object;

import lombok.Getter;
import lombok.Setter;

/**
 * Item of searching.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class SearchItem {

    private String title;
    private String url;

    @Override
    public String toString() {
        return "SearchItem{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}
