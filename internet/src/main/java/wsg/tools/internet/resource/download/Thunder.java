package wsg.tools.internet.resource.download;

import com.google.common.util.concurrent.RateLimiter;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.resource.InvalidResource;
import wsg.tools.internet.resource.entity.resource.PanResource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

/**
 * Downloader of thunder.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@SuppressWarnings("UnstableApiUsage")
public class Thunder implements Downloader {

    private static final String THUNDER_URL_PREFIX = "thunder";
    private static final RateLimiter LIMITER = RateLimiter.create(1);

    /**
     * Encode a url to a thunder url.
     */
    public static String encodeThunder(String url) {
        if (url == null) {
            return null;
        }
        if (url.startsWith(THUNDER_URL_PREFIX)) {
            return url;
        }
        byte[] bytes = ("AA" + url + "ZZ").getBytes(Constants.UTF_8);
        return String.format("%s://%s", THUNDER_URL_PREFIX, Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * Decode a thunder to a common url.
     */
    public static String decodeThunder(@Nonnull String url) {
        if (url.startsWith(THUNDER_URL_PREFIX)) {
            byte[] bytes = Base64.getDecoder().decode(url.substring(10));
            url = new String(bytes, Constants.UTF_8);
            url = StringUtils.strip(url, "AAZZ");
        }
        return url.strip();
    }

    public static Filetype[] downloadingTypes() {
        return new Filetype[]{Filetype.CFG, Filetype.XLTD};
    }

    @Override
    public boolean addTask(File dir, AbstractResource resource) throws IOException {
        if (resource instanceof InvalidResource || resource instanceof PanResource) {
            return false;
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }
        LIMITER.acquire();
        SystemUtils.openUrl(encodeThunder(resource.getUrl()));
        return true;
    }
}
