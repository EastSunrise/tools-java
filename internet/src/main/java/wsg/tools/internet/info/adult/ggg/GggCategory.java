package wsg.tools.internet.info.adult.ggg;

import java.io.Serializable;
import java.util.Objects;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * A category on the site.
 *
 * @author Kingen
 * @see GggSite#findAllCategories()
 * @since 2021/4/13
 */
public final class GggCategory implements IntCodeSupplier, Serializable {

    public static final GggCategory ALL_NO_MOSAIC = new GggCategory(178, false);
    public static final GggCategory ALL_MOSAIC = new GggCategory(179, true);
    public static final GggCategory ALL_ZH_MOSAIC = new GggCategory(180, true);

    private static final long serialVersionUID = 5671201876956458722L;

    private final int code;
    private final boolean mosaic;
    private String displayName;

    GggCategory(int code, boolean mosaic) {
        this.code = code;
        this.mosaic = mosaic;
    }

    GggCategory(int code, boolean mosaic, String displayName) {
        this.code = code;
        this.mosaic = mosaic;
        this.displayName = displayName;
    }

    public static GggCategory[] all() {
        return new GggCategory[]{ALL_NO_MOSAIC, ALL_MOSAIC, ALL_ZH_MOSAIC};
    }

    @Override
    public int getCode() {
        return code;
    }

    public boolean isMosaic() {
        return mosaic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GggCategory category = (GggCategory) o;
        return code == category.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    public String getDisplayName() {
        return displayName;
    }
}
