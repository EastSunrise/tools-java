package wsg.tools.internet.info.adult.common;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * If the adult video has mosaic.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum Mosaic implements TitleSupplier {

    /**
     * If the adult video is covered with mosaic or not.
     */
    COVERED("有码"),
    UNCOVERED("无码");

    private final String title;

    Mosaic(String title) {this.title = title;}

    @Override
    public String getTitle() {
        return title;
    }
}
