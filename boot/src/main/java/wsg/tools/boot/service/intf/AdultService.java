package wsg.tools.boot.service.intf;

import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.info.adult.view.AmateurJaAdultEntry;
import wsg.tools.internet.info.adult.view.JaAdultEntry;
import wsg.tools.internet.info.adult.west.WesternAdultEntry;
import wsg.tools.internet.info.adult.wiki.CelebrityWikiSite;
import wsg.tools.internet.info.adult.wiki.WikiAdultEntry;
import wsg.tools.internet.info.adult.wiki.WikiCelebrity;
import wsg.tools.internet.info.adult.wiki.WikiCelebrityIndex;

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
     * Import latest adult entries from a pageable repository under the domain. The entities updated
     * earlier will be saved priorly. It may cost too much memory when importing firstly.
     *
     * @param domain      the domain of the repository
     * @param subtype     the subtype whose records are going to import
     * @param pageable    the function to retrieve a paged result by a given request
     * @param firstReq    the first request with pagination information which should be specified in
     *                    reverse order by the update time
     * @param retrievable the core function to retrieve an entity in the repository
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    <I, T extends IntIdentifier & JaAdultEntry & UpdateDatetimeSupplier, P extends PageReq>
    void importLatestByPage(String domain, int subtype, RepoPageable<P, PageResult<I, P>> pageable,
        P firstReq, RepoRetrievable<I, T> retrievable) throws OtherResponseException;

    /**
     * Import entries from {@link CelebrityWikiSite}.
     *
     * @param domain      the domain of the site.
     * @param retrievable the function to retrieve an entry by the specified serial number
     * @param repository  the repository that contains all celebrities of the site
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    void importCelebrityEntries(String domain, RepoRetrievable<String, WikiAdultEntry> retrievable,
        ListRepository<WikiCelebrityIndex, WikiCelebrity> repository) throws OtherResponseException;

    /**
     * Saves a Japanese entry.
     *
     * @param entry  the entry to be saved
     * @param source the source of the entry
     * @return 0 if failed or 1 if succeeded
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
