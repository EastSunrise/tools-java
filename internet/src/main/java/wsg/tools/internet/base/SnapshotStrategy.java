package wsg.tools.internet.base;

/**
 * Strategy of updating snapshots.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public interface SnapshotStrategy {

    /**
     * Never update snapshots.
     */
    SnapshotStrategy NEVER_UPDATE = content -> false;
    /**
     * Always update snapshots.
     */
    SnapshotStrategy ALWAYS_UPDATE = content -> true;

    /**
     * Predicate whether to update current snapshot based on the content.
     *
     * @param content content of current snapshot
     * @return whether to update the snapshot
     */
    boolean ifUpdate(String content);
}
