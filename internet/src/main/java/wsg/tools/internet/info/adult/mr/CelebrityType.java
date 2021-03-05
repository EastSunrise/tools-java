package wsg.tools.internet.info.adult.mr;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Type of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum CelebrityType implements TextSupplier {
    /**
     * @see <a href="http://www.mrenbaike.net/yule/">Entertainment Stars</a>
     */
    ENTERTAINMENT("yule"),
    /**
     * @see <a href="http://www.mrenbaike.net/tiyu/">Sports Stars</a>
     */
    SPORTS("tiyu"),
    /**
     * @see <a href="http://www.mrenbaike.net/politics/">Politics Stars</a>
     */
    POLITICS("politics"),
    /**
     * @see <a href="http://www.mrenbaike.net/finance/">Finance Stars</a>
     */
    FINANCE("finance"),
    /**
     * @see <a href="http://www.mrenbaike.net/wenxue/">Literature Stars</a>
     */
    LITERATURE("wenxue"),
    /**
     * @see <a href="http://www.mrenbaike.net/shehui/">Society Stars</a>
     */
    SOCIETY("shehui"),
    /**
     * @see <a href="http://www.mrenbaike.net/net/">Internet Stars</a>
     */
    INTERNET("net"),
    /**
     * @see <a href="http://www.mrenbaike.net/other/">Other Stars</a>
     */
    OTHER("other");

    private final String text;

    CelebrityType(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
