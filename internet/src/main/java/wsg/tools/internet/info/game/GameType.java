package wsg.tools.internet.info.game;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Various games.
 *
 * @author Kingen
 * @since 2021/3/13
 */
public enum GameType implements IntCodeSupplier {
    /**
     * @see <a href="https://lol.qq.com/main.shtml">League of Legends</a>
     */
    LOL(1),
    /**
     * @see <a href="https://pvp.qq.com/">Arena of Valor</a>
     */
    PVP(2),
    /**
     * @see <a href="https://pubg.qq.com/main.shtml">PLAYERUNKNOWN’S BATTLEGROUNDS</a>
     */
    PUBG(3),
    /**
     * @see <a href="https://gp.qq.com/main.shtml">Game For Peace</a>
     */
    GP(7);

    private final int id;

    GameType(int id) {
        this.id = id;
    }

    @Override
    public Integer getCode() {
        return id;
    }
}
