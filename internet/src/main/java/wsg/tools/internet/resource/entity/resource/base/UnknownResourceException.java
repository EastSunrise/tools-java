package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

/**
 * Unknown resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
@Getter
public class UnknownResourceException extends InvalidResourceException {

    public UnknownResourceException(String reason, String title, String url) {
        super(reason, title, url);
    }

    public UnknownResourceException(String reason, String title, String url, String password) {
        super(reason, title, url, password);
    }
}
