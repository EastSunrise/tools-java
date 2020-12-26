package wsg.tools.internet.resource.entity.rrys.common;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * Resource types.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public enum ResourceTypeEnum implements TitleSupplier {

    MOVIE("电影"),
    SERIES_USA("美剧"),
    SERIES_UK("英剧"),
    SERIES_JAPAN("日剧"),
    SERIES_KOREA("韩剧"),
    SERIES_CANADA("加剧"),
    OPEN_CLASS("公开课"),
    ;

    private final String title;

    ResourceTypeEnum(String title) {this.title = title;}

    @Override
    public String getTitle() {
        return title;
    }
}
