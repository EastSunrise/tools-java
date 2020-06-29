package wsg.tools.internet.base;

/**
 * The content type of response.
 * <p>
 * JSON if apis are provided. Otherwise, HTML by crawler.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public enum ContentTypeEnum {
    /**
     * Content type of JSON
     */
    JSON,

    /**
     * Content type of HTML
     */
    HTML;

    public String getSuffix() {
        return "." + name().toLowerCase();
    }
}
