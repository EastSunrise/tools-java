package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

/**
 * Invalid resources.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class InvalidResource implements Resource {

    private final String title;
    private final String url;
    private final String reason;

    public InvalidResource(String title, String url, String reason) {
        this.title = title;
        this.url = url;
        this.reason = reason;
    }
}
