package wsg.tools.internet.video.site.adult;

/**
 * An adult video.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public class MidnightAdultVideo extends BaseAdultVideo {

    private final String cover;

    MidnightAdultVideo(String code, String cover) {
        super(code);
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }
}
