package wsg.tools.boot.entity.base.enums;

import wsg.tools.common.jackson.intf.TextSerializable;
import wsg.tools.common.jackson.intf.TitleSerializable;

/**
 * This is an example of common enum. Never use it.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public enum AbstractEnum implements TextSerializable, TitleSerializable {
    ;

    private String text;
    private String title;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
