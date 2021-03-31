package wsg.tools.internet.base.support;

import java.util.function.Function;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import org.jsoup.nodes.Document;
import wsg.tools.internet.base.SnapshotStrategy;

/**
 * Common implementations of {@code SnapshotStrategy}.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public final class SnapshotStrategies {

    private SnapshotStrategies() {
    }

    /**
     * Never updates the snapshots.
     *
     * @return the strategy
     */
    @Nonnull
    @Contract(pure = true)
    public static <V> SnapshotStrategy<V> never() {
        return v -> false;
    }

    /**
     * Always updates the snapshots.
     *
     * @return the strategy
     */
    @Nonnull
    @Contract(pure = true)
    public static <V> SnapshotStrategy<V> always() {
        return v -> true;
    }

    @Nonnull
    @Contract(value = "_ -> new", pure = true)
    public static <ID>
    WithoutNextDocument<ID> withoutNext(@Nonnull Function<Document, ID> getNext) {
        return new WithoutNextDocument<>(getNext);
    }
}
