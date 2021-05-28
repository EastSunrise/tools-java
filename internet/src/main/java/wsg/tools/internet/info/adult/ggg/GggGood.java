package wsg.tools.internet.info.adult.ggg;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.Describable;
import wsg.tools.internet.common.TitleSupplier;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.ImageSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * A good on the site.
 *
 * @author Kingen
 * @see GggSite#findAll(GggReq, PageIndex)
 * @since 2021/4/13
 */
public class GggGood implements IntIdentifier, CodeSupplier,
    TitleSupplier, UpdateDateSupplier, ImageSupplier, Tagged, ActressSupplier, Describable {

    private final int id;
    private final String code;
    private final String title;
    private final boolean mosaic;
    private final LocalDate publish;
    private URL image;
    private Set<String> tags = new HashSet<>();
    private String distributor;
    private List<String> actresses;
    private String description;

    GggGood(int id, String code, String title, boolean mosaic, LocalDate publish) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.mosaic = mosaic;
        this.publish = publish;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public boolean isMosaic() {
        return mosaic;
    }

    @Override
    public LocalDate getUpdate() {
        return publish;
    }

    @Override
    public URL getImageURL() {
        return image;
    }

    void setImage(URL image) {
        this.image = image;
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return tags;
    }

    void setTags(Set<String> tags) {
        this.tags = tags;
    }

    public String getDistributor() {
        return distributor;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return actresses;
    }

    void setActresses(List<String> actresses) {
        this.actresses = actresses;
    }

    @Override
    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }
}
