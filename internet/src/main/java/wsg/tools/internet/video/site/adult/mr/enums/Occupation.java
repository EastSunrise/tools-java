package wsg.tools.internet.video.site.adult.mr.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;

/**
 * Occupations of  a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Occupation implements AkaPredicate<String> {

    ACTOR("演员", "演員", "影视演员", "影视剧演员", "电影演员", "电视演员", "女演员", "国家一级演员", "国家二级演员", "影视明星", "青年演员",
            "香港演员", "替身演员", "影视演员(影视)", "中国影视演员", "动作演员", "内地影视演员", "演艺明星", "特型演员", "著名演员", "喜剧演员",
            "演艺演员", "中国国家话剧院演员", "女优"),
    ADULT_ACTRESS("AV女优", "日本AV女优", "艾薇女优", "AV女优秀", "av女优"),
    SINGER("歌手", "歌唱家", "创作歌手", "唱作歌手", "女歌手", "职业歌手", "流行歌手", "原创歌手", "摇滚歌手", "创作型歌手"),
    HOST("主持人", "主持", "节目主持人", "节目主持", "电视主持人", "电视节目主持人", "电视节目主持", "电台主持", "电台节目主持人"),
    MODEL("模特", "平面模特", "模特儿", "广告模特", "时装模特", "车模", "写真模特", "杂志模特", "泳装模特", "名模", "人体模特", "AV名模",
            "专业模特", "书模"),
    DIRECTOR("导演", "电影导演", "影视导演", "动画导演", "配音导演", "话剧导演", "著名导演"),
    SCREENWRITER("编剧"),
    ARTIST("艺人", "艺术家", "香港艺人"),
    SPORTS_STAR("体育明星"),
    VOICE_ACTOR("配音演员", "声优", "配音", "配音员", "日本声优", "网络声优"),
    STUDENT("学生", "大学生"),
    TEACHER("教师", "老师"),
    WRITER("作家", "作者", "文学作家"),
    PRODUCER("制片人", "制作人", "制片", "影视制作人", "电影制作人"),
    MUSICIAN("音乐人", "音乐家", "独立音乐人"),
    MUSIC_PRODUCER("音乐制作人"),
    PHOTO_ACTOR("写真女优", "写真偶像", "写真女星", "COSER"),
    CROSSTALK_ACTOR("相声演员", "相声表演艺术家"),
    BEIJING_OPERA_ACTOR("京剧演员", "京剧老生", "著名京剧演员"),
    DRAMA_ACTOR("话剧演员"),
    DANCER("舞者", "舞蹈家", "舞蹈演员"),
    COMPOSER("作曲家", "作曲人"),
    POLITICIAN("政治家"),
    BUSINESSMAN("商人"),
    ANCHOR("主播", "实况主播", "女主播"),
    NURSE("护士"),
    AIR_HOSTESS("空姐");

    private final String[] aka;

    Occupation(String... aka) {this.aka = aka;}

    @Override
    public boolean alsoKnownAs(String other) {
        return ArrayUtils.contains(aka, other);
    }
}
