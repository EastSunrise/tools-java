package wsg.tools.internet.base.page;

/**
 * Interface for pagination information.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageRequest {

    /**
     * Returns the page to be returned.
     *
     * @return the page to be returned
     */
    int getCurrent();

    /**
     * Returns the number of items to be returned.
     *
     * @return the number of items of that page
     */
    int getPageSize();

    /**
     * Returns the request requesting the next result.
     *
     * @return the next request
     */
    PageRequest next();

    /**
     * Returns the request requesting the previous result.
     *
     * @return the previous request
     */
    PageRequest previous();
}
