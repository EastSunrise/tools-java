package wsg.tools.boot.pojo.base;

import lombok.Getter;

import java.io.IOException;

/**
 * Exceptions when accessing to a {@link wsg.tools.internet.base.BaseSite}
 * except common exceptions.
 *
 * @author Kingen
 * @since 2020/11/22
 */
public class SiteException extends IOException {

    @Getter
    private final String site;

    public SiteException(String site, String message) {
        super(message);
        this.site = site;
    }
}
