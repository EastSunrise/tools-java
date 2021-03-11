package wsg.tools.internet.base.intf;

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
     */
    T handleContent(String content);

    /**
     * Obtains suffix of the target type.
     *
     * @return the suffix
     */
    String suffix();
}