package wsg.tools.boot.service.intf;

import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.info.adult.midnight.MidnightEntryType;
import wsg.tools.internet.info.adult.midnight.MidnightSite;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultService {

    /**
     * Import latest adult entries from {@link wsg.tools.internet.info.adult.LaymanCatSite}.
     *
     * @throws HttpResponseException if an error occurs when do request
     */
    void importLaymanCatSite() throws HttpResponseException;

    /**
     * Import latest adult entries from {@link MidnightSite}.
     *
     * @param type type of entries in the {@link MidnightSite}.
     * @throws HttpResponseException if an error occurs when do request
     */
    void importMidnightEntries(MidnightEntryType type) throws HttpResponseException;
}
