package wsg.tools.internet.base;

/**
 * Exception if the status of the site is abnormal.
 *
 * @author Kingen
 * @since 2020/12/30
 */
public class SiteStatusException extends RuntimeException {

    private final SiteStatus status;

    public SiteStatusException(SiteStatus status) {
        super("The status of the site is " + status.status());
        this.status = status;
    }

    public SiteStatus getStatus() {
        return status;
    }
}
