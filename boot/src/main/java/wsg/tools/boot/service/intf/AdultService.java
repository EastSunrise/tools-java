package wsg.tools.boot.service.intf;

import java.util.Optional;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.JaAdultActressAdapter;
import wsg.tools.boot.dao.api.adapter.WesternAdultEntry;
import wsg.tools.boot.pojo.entity.base.FailureReason;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.internet.info.adult.view.JaAdultEntry;

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
     * Saves a western entry.
     *
     * @param entry  the entry to be saved
     * @param source the source of the entry
     * @return {@code Optional#empty()} if succeeds, otherwise a failure reason
     */
    Optional<FailureReason> saveWesternAdultEntry(WesternAdultEntry entry, Source source);
}
