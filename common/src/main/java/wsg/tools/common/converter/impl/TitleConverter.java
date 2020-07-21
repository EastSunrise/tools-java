package wsg.tools.common.converter.impl;

import wsg.tools.common.converter.base.BaseConverter;
import wsg.tools.common.jackson.intf.TitleSupplier;

/**
 * Convert an object implementing {@link TitleSupplier} to a string.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class TitleConverter extends BaseConverter<TitleSupplier, String> {

    public static final TitleConverter INSTANCE = new TitleConverter();

    public TitleConverter() {
        super(TitleSupplier.class, String.class);
    }

    @Override
    public String convert(TitleSupplier source) {
        return source.getTitle();
    }
}
