package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

/**
 * Invalid resources.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class InvalidResourceException extends Exception implements Resource {

    private final String title;
    private final String url;
    private String password;

    public InvalidResourceException(String reason, String title, String url) {
        super(reason);
        this.title = title;
        this.url = url;
    }

    public InvalidResourceException(String reason, String title, String url, String password) {
        super(reason);
        this.title = title;
        this.url = url;
        this.password = password;
    }
}
