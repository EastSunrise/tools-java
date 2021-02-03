package wsg.tools.internet.resource.download;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.Rundll32;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.impl.BaiduDiskResource;
import wsg.tools.internet.resource.entity.resource.impl.UcDiskResource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Downloader of thunder.
 *
 * @author Kingen
 * @see <a href="https://www.xunlei.com/">Thunder</a>
 * @since 2020/10/8
 */
public class Thunder implements Downloader<Resource> {

    public static final String PREFIX = "thunder://";
    public static final String EMPTY_LINK = "thunder://QUFaWg==";
    private static final Pattern THUNDER_REGEX = Pattern.compile("thunder://(?<c>([\\w+/-]{4})+([\\w+/-]{2}[\\w+/-=]=)?)/?", Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_URL_REGEX = Pattern.compile("AA(?<url>.*)ZZ");

    /**
     * Encode a url to a thunder url.
     */
    public static String encodeThunder(@Nonnull String url) {
        if (StringUtils.startsWithIgnoreCase(url, PREFIX)) {
            return url;
        }
        byte[] bytes = ("AA" + url + "ZZ").getBytes(Constants.UTF_8);
        return PREFIX + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decode a thunder to a common url.
     */
    public static String decodeThunder(@Nonnull String url, Charset charset) {
        url = url.strip();
        while (StringUtils.startsWithIgnoreCase(url, PREFIX)) {
            url = RegexUtils.matchesOrElseThrow(THUNDER_REGEX, url).group("c");
            url = new String(Base64.getDecoder().decode(url.getBytes(charset)), charset);
            url = RegexUtils.matchesOrElseThrow(SRC_URL_REGEX, url).group("url").strip();
        }
        return url;
    }

    /**
     * Filter temporary files of thunder application.
     */
    public static SuffixFileFilter tmpFileFilter() {
        return new SuffixFileFilter("xltd", IOCase.INSENSITIVE);
    }

    /**
     * @param dir target directory is dependent on the selection of the dialog
     */
    @Override
    public boolean addTask(File dir, Resource resource) throws IOException {
        if (resource instanceof BaiduDiskResource || resource instanceof UcDiskResource) {
            return false;
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }
        return Rundll32.open(encodeThunder(resource.getUrl())) == 0;
    }
}
