package wsg.tools.internet.base.page;

/**
 * Indicates that the number of total pages is available.
 *
 * @author Kingen
 * @since 2021/3/31
 */
public interface PageCountable {

    /**
     * Returns the number of total pages.
     *
     * @return the number of total pages
     */
    int getTotalPages();
}
