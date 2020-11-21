package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.BaseItem;

import javax.annotation.Nonnull;
import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    public final Set<I> findAll() {
        return getAllUris().stream().map(uri -> {
            try {
                return getItem(uri);
            } catch (NotFoundException e) {
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Search all available items by the given keyword.
     * <p>
     * Ignore items which are not found.
     */
    public final Set<I> search(String keyword) {
        Set<I> items = new HashSet<>();
        for (URI uri : searchItems(keyword)) {
            try {
                items.add(getItem(uri));
            } catch (NotFoundException ignored) {
            }
        }
        return items;
    }

    /**
     * Obtains uris of all available items
     *
     * @return set of uris
     */
    protected abstract List<URI> getAllUris();

    /**
     * Search items for the given keyword.
     *
     * @param keyword keyword to search
     * @return set of uris of searched items
     */
    protected abstract Set<URI> searchItems(@Nonnull String keyword);

    /**
     * Obtains the item of the given uri.
     *
     * @param uri uri of target item
     * @return the item
     * @throws NotFoundException if not found
     */
    protected abstract I getItem(@Nonnull URI uri) throws NotFoundException;
}
