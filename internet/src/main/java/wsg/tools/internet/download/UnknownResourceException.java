package wsg.tools.internet.download;

import lombok.Getter;
import wsg.tools.internet.download.base.AbstractLink;

/**
 * Exceptions thrown when the target is not a link of known types.
 *
 * @author Kingen
 * @since 2020/11/1
 */
@Getter
public class UnknownResourceException extends InvalidResourceException {

    public UnknownResourceException(Class<? extends AbstractLink> clazz, String title, String url) {
        super("Not a url of " + clazz, title, url);
    }

    public UnknownResourceException(String reason, String title, String url) {
        super(reason, title, url);
    }
}
