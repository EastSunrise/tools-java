package wsg.tools.internet.download.support;

/**
 * Exceptions thrown when the target is not a link of known types.
 *
 * @author Kingen
 * @since 2020/11/1
 */
public class UnknownResourceException extends InvalidResourceException {

    private static final long serialVersionUID = 3821006025051090082L;

    public UnknownResourceException(Class<? extends AbstractLink> clazz, String title, String url) {
        super("Not a url of " + clazz, title, url);
    }

    public UnknownResourceException(String reason, String title, String url) {
        super(reason, title, url);
    }
}
