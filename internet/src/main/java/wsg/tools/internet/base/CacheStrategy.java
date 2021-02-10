package wsg.tools.internet.base;

/**
 * Strategy of updating caches.
 *
 * @author Kingen
 * @since 2021/2/4
 */
public interface CacheStrategy {

    /**
     * Never update caches.
     */
    CacheStrategy NEVER_UPDATE = content -> false;
    /**
     * Always update caches.
     */
    CacheStrategy ALWAYS_UPDATE = content -> true;

    /**
     * Predicate whether to update current caches based on the content.
     *
     * @param content cached content
     * @return whether to update the cache
     */
    boolean ifUpdate(String content);
}
