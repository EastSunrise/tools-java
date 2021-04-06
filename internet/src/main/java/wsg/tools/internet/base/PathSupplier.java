package wsg.tools.internet.base;

/**
 * Represents a supplier of a path used to build a url.
 *
 * @author Kingen
 * @since 2021/4/3
 */
public interface PathSupplier {

    /**
     * Returns the path.
     *
     * @return the path
     */
    String getAsPath();
}
