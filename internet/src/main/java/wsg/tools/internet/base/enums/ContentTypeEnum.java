package wsg.tools.internet.base.enums;

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
    HTML,
    /**
     * Content type of JavaScript
     */
    JS;

    public String getSuffix() {
        return "." + name().toLowerCase();
    }
}
