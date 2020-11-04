package wsg.tools.boot.dao.api.intf;

import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.internet.resource.entity.item.BaseItem;
import wsg.tools.internet.resource.site.BaseResourceSite;

import java.io.File;

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
     * Download the given movie to target directory.
     *
     * @param movie  entity of the given movie
     * @param target target directory
     * @return count of added resources
     */
    long download(MovieEntity movie, File target);

    /**
     * Download the given season to target directory.
     *
     * @param season entity of the given season
     * @param target target directory
     * @return count of added resources
     */
    long download(SeasonEntity season, File target);
}
