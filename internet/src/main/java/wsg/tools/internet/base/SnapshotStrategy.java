package wsg.tools.internet.base;

/**
 * Strategy of updating snapshots.
 * <p>
 * todo update snapshots regularly, such as every day
 *
 * @author Kingen
 * @since 2021/2/4
 */
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
        return v -> false;
    }

    /**
     * Predicate whether to update current snapshot based on the value.
     *
     * @param t value of current snapshot
     * @return whether to update the snapshot
     */
    boolean ifUpdate(T t);
}
