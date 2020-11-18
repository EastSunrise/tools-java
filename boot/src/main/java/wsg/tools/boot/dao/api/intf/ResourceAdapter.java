package wsg.tools.boot.dao.api.intf;

import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.internet.resource.entity.item.BaseItem;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.site.BaseResourceSite;

import java.io.File;
import java.util.Set;

/**
 * Adapter for resources to transfer result of resource sites.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public interface ResourceAdapter {

    /**
     * Import all resources from the given site.
     *
     * @param site an implementation of {@link BaseResourceSite<I>}
     */
    <I extends BaseItem> void importAll(BaseResourceSite<I> site);

    /**
     * Search resources of the given movie.
     *
     * @param movie entity of the given movie
     * @return set of searched resources
     */
    Set<BaseValidResource> search(MovieEntity movie);

    /**
     * Search resources of the given season.
     *
     * @param season entity of the given season
     * @return set of searched resources
     */
    Set<BaseValidResource> search(SeasonEntity season);

    /**
     * Download the given resources to target directory.
     *
     * @param resources set of resources to download
     * @param target    target directory
     * @return count of added resources
     */
    long download(Set<BaseValidResource> resources, File target);
}
