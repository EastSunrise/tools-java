package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * The subtype that an item belongs to in the {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum Y80sType implements IntCodeSupplier, TextSupplier {
    ;

    private final int id;
    private final String text;

    Y80sType(int id, String text) {
        this.id = id;
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
