package wsg.tools.internet.base;

/**
 * An extension of {@link ResponseWrapper} which returns a boolean that indicates whether this
 * content is latest or read from a snapshot along with the headers and the content of the
 * response.
 *
 * @author Kingen
 * @since 2021/4/9
 */
public interface CacheResponseWrapper<T> extends ResponseWrapper<T> {

    /**
     * Returns whether this content is read from a snapshot.
     *
     * @return {@code true} if this content is read from a snapshot otherwise {@code false}
     */
    boolean isSnapshot();
}
