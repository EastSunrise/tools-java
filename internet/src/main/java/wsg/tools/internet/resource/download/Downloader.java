package wsg.tools.internet.resource.download;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.SystemUtils;
import wsg.tools.internet.resource.entity.*;
import wsg.tools.internet.resource.site.AbstractVideoResourceSite;
import wsg.tools.internet.resource.site.XlcSite;
import wsg.tools.internet.resource.site.Y80sSite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.time.Year;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

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
            "rmvb", "avi", "mov"
    };
    public static final String[] VIDEO_SUFFIXES = ArrayUtils.addAll(GOOD_VIDEO_SUFFIXES, OTHER_VIDEO_SUFFIXES);
    private static final String PAN_HOST = "pan.baidu.com";
    private static final String THUNDER_URL_PREFIX = "thunder";

    private final List<AbstractVideoResourceSite<? extends TitleDetail>> SITES = Arrays.asList(
            new Y80sSite(), new XlcSite()
    );

    public Downloader(final String siteCdn) {
        SITES.forEach(site -> site.setCdn(siteCdn));
    }

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
        return Arrays.stream(schemes).anyMatch(suffix -> lowerCase.startsWith(suffix.toLowerCase() + "://"));
    }

    /**
     * Search and download resources of the given movie.
     *
     * @param dir  target directory, create if not exist
     * @param dbId may null
     * @return count of resources downloading
     */
    public int downloadMovie(@Nonnull File dir, @Nonnull String title, @Nonnull Year year, @Nullable Long dbId) {
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new SecurityException("Can't create dir " + dir.getPath());
        }
        int count = 0;
        for (AbstractVideoResourceSite<? extends TitleDetail> site : SITES) {
            try {
                for (AbstractResource resource : site.collectMovie(title, year, dbId)) {
                    if (filterResource(resource)) {
                        try {
                            SystemUtils.openUrl(encodeThunder(resource.getUrl()));
                            count++;
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
        return count;
    }

    private boolean filterResource(AbstractResource resource) {
        if (resource instanceof InvalidResource) {
            return false;
        }
        if (resource instanceof Ed2kResource) {
            return isSuffix(((Ed2kResource) resource).getFilename(), VIDEO_SUFFIXES);
        }
        if (resource instanceof MagnetResource) {
            return isSuffix(((MagnetResource) resource).getDisplayName(), VIDEO_SUFFIXES);
        }
        if (resource instanceof HttpResource) {
            return isSuffix(((HttpResource) resource).getFilename(), VIDEO_SUFFIXES) ||
                    isSuffix(((HttpResource) resource).getFilename(), "torrent");
        }
        return false;
    }
}