package wsg.tools.internet.info.adult.ggg;

import java.net.URL;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.ImageSupplier;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * The view of a good on the site.
 *
 * @author Kingen
 * @see GggSite#findById(Integer)
 * @since 2021/4/13
 */
public class GggGoodView
    implements IntIdentifier, TitledAdultEntry, CoverSupplier, Describable, ImageSupplier {

    private final int id;
    private final String title;
    private final URL cover;
    private final String description;
    private URL image;

    GggGoodView(int id, String title, URL cover, String description) {
        this.id = id;
        this.title = title;
        this.cover = cover;
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public URL getImageURL() {
        return image;
    }

    void setImage(URL image) {
        this.image = image;
    }
}
