package wsg.tools.internet.base.intf;

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
     * Returns the {@link PageRequest} requesting the next {@link PageResult}.
     *
     * @return the {@link PageRequest}
     */
    PageRequest next();

    /**
     * Returns the {@link PageRequest} requesting the previous {@link PageResult}.
     *
     * @return the {@link PageRequest}
     */
    PageRequest previous();
}
