package wsg.tools.boot.service.intf;

import javax.annotation.Nonnull;
import org.apache.http.client.HttpResponseException;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.internet.info.adult.LaymanCatSite;
import wsg.tools.internet.info.adult.midnight.MidnightLaymanEntryType;
import wsg.tools.internet.info.adult.midnight.MidnightSite;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultService {

    /**
     * Import latest adult entries from {@link LaymanCatSite}.
     *
     * @param site an instance of {@link LaymanCatSite}
     * @throws OtherHttpResponseException if an unexpected {@link HttpResponseException} occurs
     */
    void importLaymanCatSite(@Nonnull LaymanCatSite site) throws OtherHttpResponseException;

    /**
     * Import latest adult entries from {@link MidnightSite}.
     *
     * @param site an instance of {@link MidnightSite}
     * @param type type of entries in the {@link MidnightSite}.
     * @throws OtherHttpResponseException if an unexpected {@link HttpResponseException} occurs
     */
    void importMidnightEntries(@Nonnull MidnightSite site, @Nonnull MidnightLaymanEntryType type)
        throws OtherHttpResponseException;
}
