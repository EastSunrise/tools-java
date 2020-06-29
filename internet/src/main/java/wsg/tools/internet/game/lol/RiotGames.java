package wsg.tools.internet.game.lol;

import wsg.tools.internet.base.BaseSite;

/**
 * todo
 *
 * @author Kingen
 * @since 2020/6/16
 */
public class RiotGames extends BaseSite {

    private String apiKey;

    public RiotGames(String apiKey) {
        super("Riot Games", "riotgames.com", 3);
        this.apiKey = apiKey;
    }
}
