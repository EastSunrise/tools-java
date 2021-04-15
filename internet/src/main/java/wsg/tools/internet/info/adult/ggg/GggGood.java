package wsg.tools.internet.info.adult.ggg;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.info.adult.view.Describable;
import wsg.tools.internet.info.adult.view.FormalJaAdultEntry;
import wsg.tools.internet.info.adult.view.ImageSupplier;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * A good on the site.
 *
 * @author Kingen
 * @see GggSite#findPage(GggPageReq)
 * @since 2021/4/13
 */
public class GggGood implements IntIdentifier, CodeSupplier<String>, FormalJaAdultEntry,
    TitledAdultEntry, UpdateDateSupplier, ImageSupplier, Tagged, Describable {

    private final int id;
    private final String code;
    private final String title;
    private final boolean mosaic;
    private final LocalDate publish;
    private URL image;
    private String[] tags;
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

    @Override
    public Boolean getMosaic() {
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
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }

    @Override
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

    @Override
    public String getSerialNum() {
        return null;
    }

    @Override
    public LocalDate getRelease() {
        return null;
    }

    @Override
    public String getProducer() {
        return null;
    }

    @Override
    public String getSeries() {
        return null;
    }
}
