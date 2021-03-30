package wsg.tools.internet.common;

import wsg.tools.internet.base.ConcreteSite;

/**
 * Exception if the status of the site is abnormal.
 *
 * @author Kingen
 * @since 2020/12/30
 */
public class SiteStatusException extends RuntimeException {

    private static final long serialVersionUID = -1889934561941062365L;

    private final ConcreteSite status;

    public SiteStatusException(ConcreteSite status) {
        super("The status of the site is " + status.status());
        this.status = status;
    }

    public ConcreteSite getStatus() {
        return status;
    }
}
