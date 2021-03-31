package wsg.tools.internet.base;

/**
 * The strategy to update snapshots which are local caches of responses to speed up requests.
 * <p>
 * todo update snapshots regularly similarly to what a search engine does
 *
 * @author Kingen
 * @since 2021/2/4
 */
@FunctionalInterface
public interface SnapshotStrategy<T> {

    /**
     * Never updates the snapshots.
     *
     * @return the strategy
     */
    static <V> SnapshotStrategy<V> never() {
        return v -> false;
    }

    /**
     * Always updates the snapshots.
     *
     * @return the strategy
     */
    static <V> SnapshotStrategy<V> always() {
        return v -> true;
    }

    /**
     * Predicate whether to update current snapshot based on the value.
     *
     * @param t value of current snapshot
     * @return whether to update the snapshot
     */
    boolean ifUpdate(T t);
}
