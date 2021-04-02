package wsg.tools.common.util.function;

/**
 * Represents a supplier of titles, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/17
 */
@FunctionalInterface
public interface TitleSupplier {

    /**
     * Returns a title.
     *
     * @return a title
     */
    String getTitle();
}
