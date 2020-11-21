package wsg.tools.internet.resource.entity.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.resource.base.Resource;

import javax.annotation.Nonnull;
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

    private final String url;
    private String title;
    private List<Resource> resources;

    public BaseItem(@Nonnull String url) {
        this.url = url;
    }

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
