package wsg.tools.internet.base;

/**
 * Indicates the status of a site.
 *
 * @author Kingen
 * @since 2021/3/29
 */
public enum SiteStatus {
    /**
     * If the site is normal to access
     */
    NORMAL,
    /**
     * If the site is blocked by GFW.
     */
    BLOCKED,
    /**
     * If the site is accessible but part of resources is restricted by security verification or
     * else.
     */
    RESTRICTED,
    /**
     * If the site is invalid.
     */
    INVALID
}
