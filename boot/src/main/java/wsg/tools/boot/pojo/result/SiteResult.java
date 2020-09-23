package wsg.tools.boot.pojo.result;

import lombok.Getter;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.internet.base.BaseSite;

/**
 * Result when doing request to a site.
 *
 * @author Kingen
 * @since 2020/9/2
 */
@Getter
public class SiteResult<T> extends GenericResult<T> {

    private BaseSite<?> site;
    private String resource;
    private String resourceId;
    private HttpResponseException cause;

    public SiteResult(T data) {
        super(data);
    }

    public SiteResult(BaseSite<?> site, String resource, String resourceId, HttpResponseException cause) {
        super("Can't get %s from %s, id: %s, reason: %s.", resource, site.getName(), resourceId, cause.getReasonPhrase());
        this.site = site;
        this.resource = resource;
        this.resourceId = resourceId;
        this.cause = cause;
    }
}
