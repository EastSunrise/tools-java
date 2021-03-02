package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.util.function.throwable.ThrowableConsumer;
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
        // todo latest
        handleException(BdFilmSite.getInstance(), site -> {
            for (BdFilmType type : BdFilmType.values()) {
                resourceService.importIterator(site.iterator(type), site.getDomain());
            }
        });
        handleException(XlmSite.getInstance(), site -> {
            for (XlmType type : XlmType.values()) {
                resourceService.importIterator(site.iterator(type), site.getDomain());
            }
        });
        // todo if one is not found
        handleException(MovieHeavenSite.getInstance(), site -> resourceService.importIterator(site.iterator(), site.getDomain()));
        handleException(XlcSite.getInstance(), site -> resourceService.importIterator(site.iterator(), site.getDomain()));
        handleException(Y80sSite.getInstance(), site -> resourceService.importIterator(site.iterator(), site.getDomain()));
    }

    private <T> void handleException(T t, ThrowableConsumer<T, HttpResponseException> function) {
        try {
            function.accept(t);
        } catch (HttpResponseException e) {
            log.error(e.getMessage());
        }
    }
}
