package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.RangeRepository;
import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.site.*;

/**
 * Scheduled tasks.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Slf4j
@Service
public class ResourceScheduler extends BaseServiceImpl {

    private final ResourceService resourceService;

    @Autowired
    public ResourceScheduler(ResourceService resourceService) {this.resourceService = resourceService;}

    @Scheduled(cron = "0 0 0 * * ?")
    public void importLatestResources() {
        handleException(BdFilmSite.getInstance());
        handleException(MovieHeavenSite.getInstance());
        handleException(XlcSite.getInstance());
        handleException(XlmSite.getInstance());
        handleException(Y80sSite.getInstance());
    }

    private <T extends IdentifiedItem, S extends BaseSite & RangeRepository<T, Integer>> void handleException(S site) {
        try {
            resourceService.importLatest(site);
        } catch (HttpResponseException e) {
            log.error(e.getMessage());
        }
    }
}
