package wsg.tools.internet.resource.movie;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.resource.common.CoverSupplier;
import wsg.tools.internet.resource.common.StateSupplier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * An item in {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class XlcItem extends IdentifiedItem<XlcType>
    implements YearSupplier, UpdateDateSupplier, StateSupplier, CoverSupplier {

    private final LocalDate updateDate;
    private final String state;
    private URL cover;
    private Integer year;

    XlcItem(int id, @Nonnull String url, LocalDate updateDate, XlcType type, String state) {
        super(id, url, type);
        this.updateDate = Objects.requireNonNull(updateDate, "the update date of an item");
        this.state = AssertUtils.requireNotBlank(state, "the state of an item");
    }

    @Override
    public Integer getYear() {
        return year;
    }

    void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public LocalDate lastUpdate() {
        return updateDate;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = Objects.requireNonNull(cover, "the cover of an item");
    }
}
