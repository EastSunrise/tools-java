package wsg.tools.internet.resource.entity.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.common.VideoType;

import javax.annotation.Nonnull;

/**
 * Items with year and type.
 *
 * @author Kingen
 * @since 2020/10/20
 */
@Setter
@Getter
public class SimpleItem extends BaseItem {

    private Integer year;
    private VideoType type;

    public SimpleItem(@Nonnull String url) {
        super(url);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
