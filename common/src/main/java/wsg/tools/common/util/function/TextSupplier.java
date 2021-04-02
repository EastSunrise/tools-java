package wsg.tools.common.util.function;

/**
 * Represents a supplier of a text, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/19
 */
@FunctionalInterface
public interface TextSupplier {

    /**
     * Returns a text.
     *
     * @return a text
     */
    String getText();
}
