package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

/**
 * Unknown resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
@Getter
public class UnknownResource implements Resource {

    private final String title;
    private final String url;

    public UnknownResource(String title, String url) {
        this.title = title;
        this.url = url;
    }

    @Override
    public String toString() {
        return "UnknownResource{" +
                "title='" + title + '\'' +
                '}';
    }
}
