package wsg.tools.internet.common;

/**
 * Indicate the object can be converted as a parameter in the url.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public interface PathParameterized {

    /**
     * Return a string to be embedded in the url.
     *
     * @return path string
     */
    String getPath();
}
