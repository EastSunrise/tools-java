package wsg.tools.internet.base;

import wsg.tools.internet.base.exception.NotFoundException;

/**
 * Handle content of the response.
 *
 * @param <T> type of target object
 * @author Kingen
 * @since 2021/2/11
 */
public interface ContentHandler<T> {

    /**
     * Transfers the content to target object.
     *
     * @param content content to transfer
     * @return target object
     * @throws NotFoundException if not found
     */
    T handleContent(String content) throws NotFoundException;

    /**
     * Obtains suffix of the target type.
     *
     * @return suffix
     */
    String suffix();
}
