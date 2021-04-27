package wsg.tools.internet.base;

/**
 * Converts the content of the response to an object.
 *
 * @param <T> type of target object
 * @author Kingen
 * @since 2021/2/11
 */
public interface ContentConverter<T> {

    /**
     * Converts the content to an object.
     *
     * @param content the content to be converted
     * @return target object
     */
    T convert(String content);
}
