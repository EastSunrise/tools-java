package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.BaseResource;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resources of http/https/ftp, except {@link PanResource} and {@link YunResource}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class HttpResource extends BaseResource {

    public static final String[] PERMIT_SCHEMES = new String[]{"http", "https", "ftp"};
    static final String PAN_HOST = "pan.baidu.com";
    static final String YUN_HOST = "yun.cn";

    private final URL url;

    protected HttpResource(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpResource of(String title, String url) {
        if (url.contains(PAN_HOST)) {
            return PanResource.of(title, url);
        }
        if (url.contains(YUN_HOST)) {
            return YunResource.of(title, url);
        }
        try {
            url = decode(url);
            return new HttpResource(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Not a valid url: " + e.getMessage());
        }
    }

    @Override
    public String getUrl() {
        return this.url.toString();
    }
}
