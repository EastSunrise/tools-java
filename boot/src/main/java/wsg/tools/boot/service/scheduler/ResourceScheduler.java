package wsg.tools.boot.service.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.util.function.throwable.ThrowableConsumer;
import wsg.tools.internet.resource.movie.BdMovieSite;
import wsg.tools.internet.resource.movie.BdMovieType;
import wsg.tools.internet.resource.movie.GrapeSite;
import wsg.tools.internet.resource.movie.MovieHeavenSite;
import wsg.tools.internet.resource.movie.XlcSite;
import wsg.tools.internet.resource.movie.XlmSite;
import wsg.tools.internet.resource.movie.XlmType;
import wsg.tools.internet.resource.movie.Y80sSite;

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
    public ResourceScheduler(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    private static <T> void handleException(T t,
        ThrowableConsumer<T, HttpResponseException> function) {
        try {
            function.accept(t);
        } catch (HttpResponseException e) {
            log.error(e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void importLatestResources() {
        // todo latest
        handleException(new BdMovieSite(), site -> {
            for (BdMovieType type : BdMovieType.values()) {
                resourceService
                    .importIterableRepository(site.getRepository(type), site.getDomain());
            }
        });
        handleException(new XlmSite(), site -> {
            for (XlmType type : XlmType.values()) {
                resourceService
                    .importIterableRepository(site.getRepository(type), site.getDomain());
            }
        });
        // todo if one is not found
        handleException(new MovieHeavenSite(),
            site -> resourceService.importIterableRepository(site, site.getDomain()));
        handleException(new XlcSite(),
            site -> resourceService.importIterableRepository(site, site.getDomain()));
        handleException(new Y80sSite(),
            site -> resourceService.importIterableRepository(site, site.getDomain()));
        handleException(new GrapeSite(),
            site -> resourceService
                .importIterableRepository(site.getNewsRepository(), site.getDomain()));
    }
}
