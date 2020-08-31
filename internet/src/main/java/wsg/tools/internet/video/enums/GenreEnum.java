package wsg.tools.internet.video.enums;

import wsg.tools.common.function.AkaPredicate;
import wsg.tools.common.function.TextSupplier;
import wsg.tools.common.function.TitleSupplier;

/**
 * Enum for genres of subjects, with text as English display name and title as Chinese display name.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public enum GenreEnum implements TitleSupplier, TextSupplier, AkaPredicate<String> {
    /**
     * Kinds of genres
     */
    ACTION("Action", "动作"),
    ADULT("Adult", "成人"),
    ADVENTURE("Adventure", "冒险"),
    ANIMATION("Animation", "动画"),
    BIOGRAPHY("Biography", "传记"),
    CHILD("Child", "儿童"),
    COMEDY("Comedy", "喜剧"),
    COSTUME("Costume", "古装"),
    CRIME("Crime", "犯罪"),
    DISASTER("Disaster", "灾难"),
    DOCUMENTARY("Documentary", "纪录片"),
    DRAMA("Drama", "剧情"),
    EROTIC("Erotic", "情色"),
    FAMILY("Family", "家庭"),
    FANTASY("Fantasy", "奇幻"),
    GAME_SHOW("Game-Show", "电视竞赛"),
    GAY("Gay", "同性"),
    GHOST("Ghost", "鬼怪"),
    HISTORY("History", "历史"),
    HORROR("Horror", "恐怖"),
    MARTIAL_ART("Martial Art", "武侠"),
    MUSIC("Music", "音乐"),
    MUSICAL("Musical", "歌舞"),
    MYSTERY("Mystery", "悬疑"),
    REALITY_TV("Reality-TV", "真人秀"),
    ROMANCE("Romance", "爱情"),
    SCI_FI("Sci-Fi", "科幻"),
    SHORT("Short", "短片"),
    SPORT("Sport", "运动"),
    TALK_SHOW("Talk-Show", "脱口秀"),
    THRILLER("Thriller", "惊悚"),
    WAR("War", "战争"),
    WESTERN("Western", "西部"),
    NEWS("News", "资讯");

    private final String text;
    private final String title;

    GenreEnum(String text, String title) {
        this.text = text;
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return text.equals(other) || title.equals(other);
    }
}
