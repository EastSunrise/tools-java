package wsg.tools.internet.resource.entity.resource;

import lombok.Getter;
import wsg.tools.common.constant.Constants;

import java.net.URLDecoder;

/**
 * Invalid resources.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class InvalidResource extends AbstractResource {

    private final String url;

    public InvalidResource(String url) {
        try {
            url = URLDecoder.decode(url, Constants.UTF_8);
        } catch (IllegalArgumentException ignored) {
        }
        this.url = url;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String filename() {
        return null;
    }

    @Override
    public long size() {
        return -1;
    }
}
