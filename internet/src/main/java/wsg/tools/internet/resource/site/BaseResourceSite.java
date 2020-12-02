package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.base.BaseItem;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        return getAllPaths().stream().map(path -> {
            try {
                return getItem(path);
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
        for (String path : searchItems(keyword)) {
            try {
                items.add(getItem(path));
            } catch (NotFoundException ignored) {
            }
        }
        return items;
    }

    /**
     * Obtains paths of all available items
     *
     * @return set of paths
     */
    protected abstract List<String> getAllPaths();

    /**
     * Search items for the given keyword.
     *
     * @param keyword keyword to search
     * @return set of paths of searched items
     */
    protected abstract Set<String> searchItems(@Nonnull String keyword);

    /**
     * Obtains the item of the given path.
     *
     * @param path path to target item
     * @return the item
     * @throws NotFoundException if not found
     */
    protected abstract I getItem(@Nonnull String path) throws NotFoundException;

    /**
     * Create paths based on ids.
     */
    protected final List<String> getPathsById(int startInclusive, int endExclusive, IntFunction<String> creator, int... excepts) {
        if (ArrayUtils.isEmpty(excepts)) {
            return IntStream.range(startInclusive, endExclusive).mapToObj(creator).collect(Collectors.toList());
        }
        return IntStream.range(startInclusive, endExclusive).filter(i -> !ArrayUtils.contains(excepts, i))
                .mapToObj(creator).collect(Collectors.toList());
    }
}
