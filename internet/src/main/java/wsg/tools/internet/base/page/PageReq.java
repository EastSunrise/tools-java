package wsg.tools.internet.base.page;

/**
 * Interface for pagination information.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public interface PageReq {

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
     * <p>
     * Note that this method must be override to match the subclass.
     *
     * @return the next request
     */
    PageReq next();

    /**
     * Returns the request requesting the previous result.
     * <p>
     * Note that this method must be override to match the subclass.
     *
     * @return the previous request
     */
    PageReq previous();
}
