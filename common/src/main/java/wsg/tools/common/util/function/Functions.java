package wsg.tools.common.util.function;

import java.util.function.Consumer;

/**
 * Provides common functions.
 *
 * @author Kingen
 * @since 2021/6/6
 */
public final class Functions {

    private Functions() {
    }

    /**
     * Empty consumer.
     */
    public static <T> Consumer<T> emptyConsumer() {
        return t -> {
        };
    }
}
