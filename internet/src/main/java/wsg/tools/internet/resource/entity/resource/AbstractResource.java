package wsg.tools.internet.resource.entity.resource;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Base class of resources.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Getter
@Setter
public abstract class AbstractResource {

    private String title;

    /**
     * Obtains the url of this resource.
     *
     * @return url
     */
    public abstract String getUrl();

    /**
     * Obtains the file name of this resource.
     *
     * @return file name
     */
    public abstract String filename();

    /**
     * Obtains size of the resource.
     *
     * @return size, Byte as unit, -1 if unknown
     */
    public abstract long size();

    @Override
    public String toString() {
        return "AbstractResource{" +
                "title='" + title + '\'' +
                "url='" + getUrl() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractResource resource = (AbstractResource) o;
        return Objects.equals(getUrl(), resource.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
