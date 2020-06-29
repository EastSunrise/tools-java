package wsg.tools.common.jackson.intf;

/**
 * Serialize to an English text, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/19
 */
public interface TextSerializable {

    /**
     * Serialize to English text
     *
     * @return text
     */
    String getText();
}
