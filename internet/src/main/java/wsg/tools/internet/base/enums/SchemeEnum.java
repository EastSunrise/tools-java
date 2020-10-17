package wsg.tools.internet.base.enums;

/**
 * Scheme of the website.
 *
 * @author Kingen
 * @since 2020/6/15
 */
public enum SchemeEnum {
    /**
     * Protocol of the url
     */
    HTTPS,
    HTTP;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
