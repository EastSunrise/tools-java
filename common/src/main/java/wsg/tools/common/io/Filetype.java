package wsg.tools.common.io;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Type of a file, based on its suffix.
 *
 * @author Kingen
 * @since 2020/10/8
 */
public enum Filetype {
    /**
     * types
     */
    ASS,
    AVI,
    CFG,
    HTML,
    MKV,
    MP4,
    RMVB,
    SRT,
    TORRENT,
    XLTD;

    /**
     * Obtains type of the given name.
     *
     * @param name name of the file or resource
     * @return type
     */
    public static Filetype typeOf(String name) {
        if (StringUtils.isBlank(name) || !name.contains(SignEnum.DOT.toString())) {
            return null;
        }
        Filetype[] enums = Filetype.values();
        String su = name.substring(name.lastIndexOf(SignEnum.DOT.getC()) + 1);
        for (Filetype anEnum : enums) {
            if (anEnum.suffix().equalsIgnoreCase(su)) {
                return anEnum;
            }
        }
        throw new IllegalArgumentException(String.format("Unknown name %s for %s", name, Filetype.class.getName()));
    }

    public static SuffixFileFilter fileFilter(Filetype... types) {
        return new SuffixFileFilter(Arrays.stream(types).map(Filetype::suffix).collect(Collectors.toList()), IOCase.SYSTEM);
    }

    public static Filetype[] videoTypes() {
        return new Filetype[]{MP4, MKV, AVI, RMVB};
    }

    public boolean isVideo() {
        return ArrayUtils.contains(videoTypes(), this);
    }

    public String suffix() {
        return name().toLowerCase();
    }
}
