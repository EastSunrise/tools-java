package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * The subtype that an item belongs to in the {@link XlcSite}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum XlcType implements IntCodeSupplier {
    ;

    private final int id;

    XlcType(int id) {
        this.id = id;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
