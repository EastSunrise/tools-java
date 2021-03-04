package wsg.tools.internet.download.base;

/**
 * If the length of a link is available.
 *
 * @author Kingen
 * @since 2020/12/3
 */
public interface LengthSupplier {

    /**
     * Returns the length of the link.
     *
     * @return the length
     */
    Long length();
}
