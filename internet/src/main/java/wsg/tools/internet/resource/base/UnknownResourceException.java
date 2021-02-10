package wsg.tools.internet.resource.base;

import lombok.Getter;

/**
 * Exceptions thrown when the target is not a known resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
@Getter
public class UnknownResourceException extends InvalidResourceException {

    public UnknownResourceException(Class<? extends AbstractResource> clazz, String title, String url) {
        super("Not a url of " + clazz.getName(), title, url);
    }

    public UnknownResourceException(String reason, String title, String url) {
        super(reason, title, url);
    }
}
