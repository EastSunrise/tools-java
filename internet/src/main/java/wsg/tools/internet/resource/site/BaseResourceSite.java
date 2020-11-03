package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.BaseItem;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.stream.Collectors;

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
        return searchItems(keyword).stream().map(path -> {
            try {
                return getItem(path);
            } catch (NotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
        }).collect(Collectors.toSet());
    }

    /**
     * Search items for the given keyword.
     *
     * @param keyword keyword to search
     * @return set of paths of searched items
     */
    protected abstract Set<String> searchItems(@Nonnull String keyword);

    /**
     * Obtains the item of the given keyword.
     *
     * @param path path of target item
     * @return the item
     * @throws NotFoundException if not found
     */
    protected abstract I getItem(@Nonnull String path) throws NotFoundException;
}
