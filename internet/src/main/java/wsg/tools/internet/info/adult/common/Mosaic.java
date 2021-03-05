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
    COVERED(true, "有码"), UNCOVERED(false, "无码");

    private final boolean covered;
    private final String title;

    Mosaic(boolean covered, String title) {
        this.covered = covered;
        this.title = title;
    }

    public boolean isCovered() {
        return covered;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
