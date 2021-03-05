package wsg.tools.boot.service.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.pojo.result.BatchResult;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
@FunctionalInterface
public interface AdultService {

    /**
     * Import latest adult entries from {@link wsg.tools.internet.info.adult.LaymanCatSite}.
     *
     * @return result of importing
     * @throws HttpResponseException if an error occurs when do request
     */
    BatchResult<String> importLaymanCatSite() throws HttpResponseException;
}
