package wsg.tools.internet.download.support;

import lombok.Getter;

/**
 * Exception thrown when the target is not a valid link.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class InvalidResourceException extends Exception {

    private static final long serialVersionUID = -6242451143908523308L;

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
