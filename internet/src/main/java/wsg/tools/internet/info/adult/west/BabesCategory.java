package wsg.tools.internet.info.adult.west;

import java.net.URL;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.TitleSupplier;

/**
 * A category on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findAllCategories()
 * @since 2021/4/3
 */
public class BabesCategory implements PathSupplier, TitleSupplier, CoverSupplier {

    private final String titlePath;
    private final String title;
    private final URL cover;
    private final int count;

    BabesCategory(String titlePath, String title, URL cover, int count) {
        this.titlePath = titlePath;
        this.title = title;
        this.cover = cover;
        this.count = count;
    }

    @Override
    public String getAsPath() {
        return titlePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    public int getCount() {
        return count;
    }
}
