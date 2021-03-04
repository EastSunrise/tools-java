package wsg.tools.internet.download;

import lombok.Getter;
import wsg.tools.internet.download.base.AbstractLink;

/**
 * Exception thrown when the target is not a valid link.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class InvalidResourceException extends Exception {

    private final String title;
    private final String url;

    /**
     * If the url is not a valid one of the given type {@code clazz}.
     */
    public InvalidResourceException(Class<? extends AbstractLink> clazz, String title, String url) {
        super("Not a valid url of " + clazz);
        this.title = title;
        this.url = url;
    }

    public InvalidResourceException(String reason, String title, String url) {
        super(reason);
        this.title = title;
        this.url = url;
    }
}
