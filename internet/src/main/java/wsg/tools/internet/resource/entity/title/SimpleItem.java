package wsg.tools.internet.resource.entity.title;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.common.VideoTypeEnum;

/**
 * Titles of 80s.
 *
 * @author Kingen
 * @since 2020/9/23
 */
@Getter
@Setter
public class SimpleItem extends BaseItem {
    private Integer year;
    private VideoTypeEnum type;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
