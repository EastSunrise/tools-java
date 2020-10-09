package wsg.tools.internet.resource.common;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.function.AkaPredicate;

/**
 * Video types of resources.
 *
 * @author Kingen
 * @since 2020/9/9
 */
public enum VideoTypeEnum implements AkaPredicate<String> {
    /**
     * tv/movie
     */
    UNKNOWN("国语配音", "/thread-15"),
    TV(
            "ju", "大陆剧", "日韩剧", "欧美剧", "港台剧",
            "/thread-19", "/thread-20", "/thread-21", "/thread-22", "/thread-23", "/thread-24", "/thread-27", "/thread-36",
            "zy", "综艺片", "/thread-29", "/thread-30", "/thread-31", "/thread-32"
    ),
    MOVIE(
            "movie", "喜剧片", "剧情片", "动作片", "爱情片", "恐怖片", "战争片", "科幻片", "其它片",
            "1080P", "4K", "3D电影",
            "/thread-2", "/thread-3", "/thread-4", "/thread-5", "/thread-14", "/thread-17", "/thread-26"
    ),
    ANIME("dm", "动画片", "动漫"),
    TRAILER("trailer"),
    MV("mv"),
    VIDEO("video"),
    COURSE("course");

    private final String[] aka;

    VideoTypeEnum(String... aka) {
        this.aka = aka;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return ArrayUtils.contains(aka, other);
    }
}
