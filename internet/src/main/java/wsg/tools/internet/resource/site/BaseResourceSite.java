package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseDetail;
import wsg.tools.internet.resource.entity.title.BaseTitle;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

/**
 * Base class of sites of resources of video.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Slf4j
public abstract class BaseResourceSite<T extends BaseTitle, D extends BaseDetail> extends BaseSite {

    protected BaseResourceSite(String name, String host) {
        super(name, host);
    }

    protected BaseResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected BaseResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 1, postPermitsPerSecond);
    }

    /**
     * Collect all available resources by the given keyword.
     * <p>
     * The resources are not filtered.
     */
    public Set<AbstractResource> collect(String keyword) {
        Set<AbstractResource> resources = new HashSet<>();
        for (T title : search(keyword)) {
            resources.addAll(find(title).getResources());
        }
        return resources;
    }

    /**
     * Search titles for the given keyword.
     *
     * @param keyword keyword to search
     * @return list of returned items
     */
    protected abstract Set<T> search(@Nonnull String keyword);

    /**
     * Obtains details of the given title.
     *
     * @param title given title of the page of the resources
     * @return resource
     */
    protected abstract D find(@Nonnull T title);
}
