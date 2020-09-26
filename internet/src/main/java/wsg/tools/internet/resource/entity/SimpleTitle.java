package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Titles of 80s.
 *
 * @author Kingen
 * @since 2020/9/23
 */
@Getter
@Setter
public class SimpleTitle extends BaseTitle {
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
