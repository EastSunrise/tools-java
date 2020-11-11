package wsg.tools.internet.resource.download;

import wsg.tools.common.constant.Constants;
import wsg.tools.common.io.Filetype;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.SystemUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.entity.resource.valid.PanResource;
import wsg.tools.internet.resource.entity.resource.valid.YunResource;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Downloader of thunder.
 *
 * @author Kingen
 * @see <a href="https://www.xunlei.com/">Thunder</a>
 * @since 2020/10/8
 */
public class Thunder implements Downloader<BaseValidResource> {

    public static final String SCHEME = "thunder";
    public static final String EMPTY_LINK = "thunder://QUFaWg==";
    private static final Pattern DECODED_REGEX = Pattern.compile("AA(?<url>.*)ZZ");

    /**
     * Encode a url to a thunder url.
     */
    public static String encodeThunder(@Nonnull String url) {
        if (url.startsWith(SCHEME)) {
            return url;
        }
        byte[] bytes = ("AA" + url + "ZZ").getBytes(Constants.UTF_8);
        return String.format("%s://%s", SCHEME, Base64.getEncoder().encodeToString(bytes));
    }

    /**
     * Decode a thunder to a common url.
     */
    public static String decodeThunder(@Nonnull String url) {
        while (url.startsWith(SCHEME)) {
            url = url.substring((SCHEME + "://").length());
            url = new String(Base64.getDecoder().decode(url), Constants.UTF_8);
            url = RegexUtils.matchesOrElseThrow(DECODED_REGEX, url).group("url");
        }
        return url.strip();
    }

    public static Filetype[] tmpTypes() {
        return new Filetype[]{Filetype.XLTD};
    }

    /**
     * @param dir target directory is dependent on the selection of the dialog
     */
    @Override
    public boolean addTask(File dir, BaseValidResource resource) throws IOException {
        if (resource instanceof PanResource || resource instanceof YunResource) {
            return false;
        }

        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }
        Process process = SystemUtils.openUrl(encodeThunder(resource.getUrl()));
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            throw AssertUtils.runtimeException(e);
        }
        return process.exitValue() == 0;
    }
}
