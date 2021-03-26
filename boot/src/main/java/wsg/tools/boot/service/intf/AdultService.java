package wsg.tools.boot.service.intf;

import javax.annotation.Nonnull;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.LicencePlateSite;
import wsg.tools.internet.info.adult.midnight.MidnightAmateurEntryType;
import wsg.tools.internet.info.adult.midnight.MidnightSite;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultService {

    /**
     * Import latest adult entries from {@link LicencePlateSite}.
     *
     * @param site an instance of {@link LicencePlateSite}
     * @throws OtherResponseException if an unexpected {@link HttpResponseException} occurs
     */
    void importLicencePlateSite(@Nonnull LicencePlateSite site) throws OtherResponseException;

    /**
     * Import latest adult entries from {@link MidnightSite}.
     *
     * @param site an instance of {@link MidnightSite}
     * @param type type of entries in the {@link MidnightSite}.
     * @throws OtherResponseException if an unexpected {@link HttpResponseException} occurs
     */
    void importMidnightEntries(@Nonnull MidnightSite site, @Nonnull MidnightAmateurEntryType type)
        throws OtherResponseException;
}
