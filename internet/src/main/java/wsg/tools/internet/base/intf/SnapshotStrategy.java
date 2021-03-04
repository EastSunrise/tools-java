package wsg.tools.internet.base.intf;

/**
 * Strategy of updating snapshots.
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
