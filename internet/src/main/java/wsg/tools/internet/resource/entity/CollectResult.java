package wsg.tools.internet.resource.entity;

import lombok.Getter;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Result of collecting resources.
 *
 * @author Kingen
 * @since 2020/10/24
 */
@Getter
public class CollectResult<T extends BaseItem> {

    private final Set<AbstractResource> resources = new HashSet<>();
    private final List<T> excludedItems = new ArrayList<>();

    public final void include(Set<AbstractResource> resources) {
        this.resources.addAll(resources);
    }

    public final void exclude(T item) {
        excludedItems.add(item);
    }
}
