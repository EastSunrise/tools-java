package wsg.tools.internet.base.exception;

import lombok.Getter;
import wsg.tools.internet.base.SiteStatus;

/**
 * Exception if the status of the site is abnormal.
 *
 * @author Kingen
 * @since 2020/12/30
 */
public class SiteStatusException extends Exception {

    @Getter
    private final SiteStatus status;

    public SiteStatusException(SiteStatus status) {
        super("The status of the site is " + status.status());
        this.status = status;
    }

    public SiteStatusException(String message, SiteStatus status) {
        super(message);
        this.status = status;
    }
}
