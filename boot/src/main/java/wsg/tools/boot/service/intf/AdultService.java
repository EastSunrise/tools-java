package wsg.tools.boot.service.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.video.site.adult.MidnightSite;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultService {

    /**
     * Import latest adult series from {@link MidnightSite}.
     *
     * @throws HttpResponseException if an error occurs when do request
     */
    void importLatest() throws HttpResponseException;
}
