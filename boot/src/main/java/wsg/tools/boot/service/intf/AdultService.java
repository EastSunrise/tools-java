package wsg.tools.boot.service.intf;

import java.util.List;
import org.springframework.data.domain.Pageable;
import wsg.tools.boot.pojo.entity.adult.ImagePreview;
import wsg.tools.internet.base.IntIdentifier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.entry.AmateurSupplier;

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
    <T extends AmateurSupplier>
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
    <I, T extends IntIdentifier & AmateurSupplier & UpdateDatetimeSupplier, P extends PageReq>
    void importLatestByPage(String domain, int subtype, RepoPageable<P, PageResult<I, P>> pageable,
        P firstReq, RepoRetrievable<I, T> retrievable) throws OtherResponseException;

    /**
     * Retrieves images of adult entries.
     *
     * @param pageable pagination information
     * @return list of images views
     */
    List<ImagePreview> findImages(Pageable pageable);
}
