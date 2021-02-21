package wsg.tools.common.util.function;

/**
 * Represents a supplier of an English text, usually used to display.
 *
 * @author Kingen
 * @since 2020/6/19
 */
@FunctionalInterface
public interface TextSupplier {

    /**
     * Serialize to English text
     *
     * @return text
     */
    String getText();
}
