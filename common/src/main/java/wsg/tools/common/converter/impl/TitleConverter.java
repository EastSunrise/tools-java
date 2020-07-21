package wsg.tools.common.converter.impl;

import wsg.tools.common.converter.base.BaseConverter;
import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * Convert an object implementing {@link TitleSerializable} to a string.
 *
 * @author Kingen
 * @since 2020/7/21
 */
public class TitleConverter extends BaseConverter<TitleSerializable, String> {

    public static final TitleConverter INSTANCE = new TitleConverter();

    public TitleConverter() {
        super(TitleSerializable.class, String.class);
    }

    @Override
    public String convert(TitleSerializable source) {
        return source.getTitle();
    }
}
