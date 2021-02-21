package wsg.tools.common.util.function;

/**
 * Represents a supplier of a title, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/17
 */
@FunctionalInterface
public interface TitleSupplier {
    /**
     * Serialize to Chinese title
     *
     * @return title
     */
    String getTitle();
}
