package wsg.tools.common.jackson.intf;

/**
 * Serialize to Chinese title, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public interface TitleSerializable {
    /**
     * Serialize to Chinese title
     *
     * @return title
     */
    String getTitle();
}
