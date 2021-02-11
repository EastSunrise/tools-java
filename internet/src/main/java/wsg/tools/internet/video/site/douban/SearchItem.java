package wsg.tools.internet.video.site.douban;

import lombok.Getter;

/**
 * Item of searching.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
public class SearchItem {

    private final String title;
    private final String url;

    SearchItem(String title, String url) {
        this.title = title;
        this.url = url;
    }
}
