package wsg.tools.boot.service.intf;

import java.util.Optional;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.JaAdultActressAdapter;
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.west.WesternAdultEntry;

/**
 * Interface of service for adult videos.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public interface AdultService {

    /**
     * Saves a Japanese entry.
     *
     * @param entry  the entry to be saved
     * @param source the source of the entry
     * @return {@code Optional#empty()} if succeeds, otherwise a failure reason
     */
    Optional<FailureReason> saveJaAdultEntry(JaAdultEntry entry, Source source);

    /**
     * Saves an adult actress.
     *
     * @param adapter the adapter that transfers an entity to the actress
     * @param source  the source of the actress
     * @return {@code Optional#empty()} if succeeds, otherwise a failure reason
     */
    Optional<FailureReason>
    saveJaAdultActress(@Nonnull JaAdultActressAdapter adapter, @Nonnull Source source);

    /**
     * Imports latest adult entries from the specified list repository.
     *
     * @param domain     the domain of the repository
     * @param subtype    the subtype to which the entries belong to
     * @param repository the repository to be imported
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    <T extends IntIdentifier & WesternAdultEntry & UpdateTemporalSupplier<?>>
    void importIntListRepository(String domain, int subtype, ListRepository<Integer, T> repository)
        throws OtherResponseException;

    /**
     * Saves a western entry.
     *
     * @param entry  the entry to be saved
     * @param source the source of the entry
     * @return 0 if failed or 1 if succeeded
     */
    int saveWesternAdultEntry(WesternAdultEntry entry, Source source);
}
