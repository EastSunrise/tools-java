package wsg.tools.internet.resource.entity.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.resource.base.Resource;

import java.util.List;
import java.util.Objects;

/**
 * Detail of a base item.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
@Setter
public class BaseItem {

    private String title;
    private String url;
    private List<Resource> resources;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseItem that = (BaseItem) o;
        return url.equals(that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
