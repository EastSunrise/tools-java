package wsg.tools.boot.service.scheduler;

import java.util.function.Function;
import javax.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.api.support.SiteManager;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.internet.base.SiteClient;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UpdateTemporalSupplier;
import wsg.tools.internet.movie.resource.BdMovieType;
import wsg.tools.internet.movie.resource.IdentifierItem;
import wsg.tools.internet.movie.resource.ResourceRepository;
import wsg.tools.internet.movie.resource.XlcType;

/**
 * Tasks to import resources.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Slf4j
@Service
public class ResourceScheduler extends BaseServiceImpl {

    private final ResourceService resourceService;
    private final SiteManager manager;

    @Autowired
    public ResourceScheduler(ResourceService resourceService, SiteManager manager) {
        this.resourceService = resourceService;
        this.manager = manager;
    }

    @Scheduled(cron = "0 0 13 * * ?")
    public void importBdMovie() {
        importIntRange(manager.bdMovieSite(), BdMovieType::ordinal);
    }

    @Scheduled(cron = "0 30 5 * * ?")
    public void importXlc() {
        importIntRange(manager.xlcSite(), XlcType::getId);
    }

    private <E extends Enum<E>, T extends IdentifierItem<E> & UpdateTemporalSupplier<?>, S extends ResourceRepository<T> & SiteClient>
    void importIntRange(@Nonnull S site, Function<E, Integer> subtypeFunc) {
        try {
            ListRepository<Integer, T> repository = site.getRepository();
            resourceService.importIntListRepository(repository, site.getName(), subtypeFunc);
        } catch (OtherResponseException e) {
            log.error(e.getMessage());
        }
    }
}
