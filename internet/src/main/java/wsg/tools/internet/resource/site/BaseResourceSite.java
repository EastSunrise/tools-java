package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.BaseItem;

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
public abstract class BaseResourceSite<I extends BaseItem> extends BaseSite {

    protected BaseResourceSite(String name, String host) {
        super(name, host);
    }

    protected BaseResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected BaseResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 10D, postPermitsPerSecond);
    }

    /**
     * Obtains all available items on the site.
     *
     * @return set of items
     */
    public abstract Set<I> findAll();

    /**
     * Search all available items by the given keyword.
     */
    public final Set<I> search(String keyword) {
        Set<I> items = new HashSet<>();
        for (URIBuilder builder : searchItems(keyword)) {
            try {
                items.add(getItem(builder));
            } catch (NotFoundException ignored) {
            }
        }
        return items;
    }

    /**
     * Search items for the given keyword.
     *
     * @param keyword keyword to search
     * @return set of URIBuilders of searched items
     */
    protected abstract Set<URIBuilder> searchItems(@Nonnull String keyword);

    /**
     * Obtains the item of the given keyword.
     *
     * @param builder builder of uri of target item
     * @return the item
     * @throws NotFoundException if not found
     */
    protected abstract I getItem(@Nonnull URIBuilder builder) throws NotFoundException;
}
