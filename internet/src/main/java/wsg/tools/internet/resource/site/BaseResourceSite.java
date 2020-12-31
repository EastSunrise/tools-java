package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.throwable.ThrowableFunction;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.base.BaseItem;

import java.util.ArrayList;
import java.util.List;
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
     * @return all items
     */
    public abstract List<I> findAll();

    protected final List<I> findAllByPathsIgnoreNotFound(List<String> paths, ThrowableFunction<String, I, NotFoundException> getItemByPath) {
        List<I> items = new ArrayList<>();
        for (String path : paths) {
            try {
                items.add(getItemByPath.apply(path));
            } catch (NotFoundException ignored) {
            }
        }
        return items;
    }

    /**
     * Obtains paths based on ids.
     */
    protected final List<String> getPathsById(int endInclusive, String pathFormat, int... excepts) {
        IntStream intStream = IntStream.rangeClosed(1, endInclusive);
        if (ArrayUtils.isNotEmpty(excepts)) {
            intStream = intStream.filter(i -> !ArrayUtils.contains(excepts, i));
        }
        return intStream.mapToObj(id -> String.format(pathFormat, id)).collect(Collectors.toList());
    }
}
