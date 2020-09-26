package wsg.tools.internet.resource.download;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.resource.entity.*;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Predicate;

/**
 * Utility for downloading.
 *
 * @author Kingen
 * @since 2020/9/14
 */
@Slf4j
public final class Downloader {

    public static final String[] THUNDER_FILE_SUFFIXES = new String[]{
            "cfg", "xltd"
    };
    public static final String[] GOOD_VIDEO_SUFFIXES = new String[]{"mp4", "mkv"};
    public static final String[] OTHER_VIDEO_SUFFIXES = new String[]{
            "rmvb", "avi"
    };
    public static final String[] VIDEO_SUFFIXES = ArrayUtils.addAll(GOOD_VIDEO_SUFFIXES, OTHER_VIDEO_SUFFIXES);
    private static final String PAN_HOST = "pan.baidu.com";
    private static final String THUNDER_URL_PREFIX = "thunder";

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

    /**
     * Classify a url.
     *
     * @param url source url
     * @return target resource of the given url
     */
    public static AbstractResource classifyUrl(@NonNull String url) {
        url = decodeThunder(url);
        try {
            if (isScheme(url, HttpResource.PERMIT_SCHEMES)) {
                if (url.contains(PAN_HOST)) {
                    return new PanResource(url);
                }
                return new HttpResource(url);
            }
            if (isScheme(url, Ed2kResource.SCHEME)) {
                return new Ed2kResource(url);
            }
            if (isScheme(url, MagnetResource.SCHEME)) {
                return new MagnetResource(url);
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return new InvalidResource(url);
    }

    public static boolean isSuffix(final String filename, final String... suffixes) {
        if (filename == null) {
            return false;
        }
        String lowerCase = filename.toLowerCase();
        return Arrays.stream(suffixes).anyMatch(suffix -> lowerCase.endsWith(SignEnum.DOT + suffix.toLowerCase()));
    }

    public static boolean isScheme(final String str, final String... schemes) {
        if (str == null) {
            return false;
        }
        String lowerCase = str.toLowerCase();
        return Arrays.stream(schemes).anyMatch(suffix -> lowerCase.startsWith(suffix.toLowerCase() + ":"));
    }

    /**
     * Search and download resources of the given movie.
     *
     * @param dir target directory, create if not exist
     * @return count of resources downloading
     */
    public static int downloadResources(Iterable<AbstractResource> resources, File dir, Predicate<AbstractResource> filter) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }
        int count = 0;
        for (AbstractResource resource : resources) {
            if (filter.test(resource)) {
                try {
                    SystemUtils.openUrl(encodeThunder(resource.getUrl()));
                    count++;
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return count;
    }

    public static Predicate<AbstractResource> filter() {
        return resource -> {
            if (resource instanceof InvalidResource) {
                return false;
            }
            if (resource instanceof Ed2kResource) {
                return isSuffix(((Ed2kResource) resource).getFilename(), VIDEO_SUFFIXES);
            }
            if (resource instanceof MagnetResource) {
                return ((MagnetResource) resource).getDisplayName() == null ||
                        isSuffix(((MagnetResource) resource).getDisplayName(), VIDEO_SUFFIXES);
            }
            if (resource instanceof HttpResource) {
                return isSuffix(((HttpResource) resource).getFilename(), VIDEO_SUFFIXES) ||
                        isSuffix(((HttpResource) resource).getFilename(), "torrent");
            }
            return false;
        };
    }

    public static Predicate<AbstractResource> filter(final long minSize, final long maxSize) {
        if (minSize < 0 || maxSize <= minSize) {
            throw new IllegalArgumentException("Size mustn't be negative and the max one must be larger than the min one.");
        }
        return resource -> {
            if (!filter().test(resource)) {
                return false;
            }
            if (resource instanceof Ed2kResource) {
                long size = ((Ed2kResource) resource).getSize();
                return size < 0 || (size >= minSize && size <= maxSize);
            }
            if (resource instanceof MagnetResource) {
                long size = ((MagnetResource) resource).getSize();
                return size < 0 || (size >= minSize && size <= maxSize);
            }
            return true;
        };
    }
}
