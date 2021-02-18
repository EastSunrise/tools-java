package wsg.tools.internet.video.site.douban;

import lombok.Getter;

/**
 * Item of searching.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
public class SearchItem implements DoubanIdentifier {

    private final long id;
    private final String title;
    private final String url;

    SearchItem(long id, String title, String url) {
        this.id = id;
        this.title = title;
        this.url = url;
    }

    @Override
    public Long getDbId() {
        return id;
    }
}
