package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
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
     * Import latest adult entries from a linked repository under the domain.
     *
     * @param domain     the domain of the repository
     * @param subtype    the subtype whose records are going to import
     * @param repository the linked repository under the domain
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    <T extends AmateurJaAdultEntry & UpdateTemporalSupplier<?>>
    void importLinkedRepository(String domain, int subtype, LinkedRepository<String, T> repository)
        throws OtherResponseException;

    /**
     * Saves a Japanese entry.
     *
     * @param entry  the entry to be saved
     * @param source the source of the entry
     * @return 1 if succeeded, otherwise return 0
     */
    int saveJaAdultEntry(JaAdultEntry entry, Source source);

    /**
     * Imports latest adult entries from the specified list repository.
     *
     * @param domain     the domain of the repository
     * @param subtype    the subtype to which the entries belong to
     * @param repository the repository to be imported
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    <T extends IntIdentifier & WesternAdultEntry>
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
