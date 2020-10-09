package wsg.tools.internet.resource.entity.title;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Item obtained by searching, including necessary properties.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Getter
@Setter
public class BaseTitle {

    private String title;
    private String path;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseTitle baseTitle = (BaseTitle) o;
        return path.equals(baseTitle.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }
}
