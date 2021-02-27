package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;

import java.util.List;

/**
 * An adult video.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class AdultVideo extends SimpleAdultVideo {

    private final String cover;
    private SimpleCelebrity celebrity;
    private String actress;
    private String director;
    private String producer;
    private String series;
    private List<String> tags;

    AdultVideo(String code, String cover) {
        super(code);
        this.cover = cover;
    }

    void setCelebrity(SimpleCelebrity celebrity) {
        this.celebrity = celebrity;
    }

    void setActress(String actress) {
        this.actress = actress;
    }

    void setDirector(String director) {
        this.director = director;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    void setSeries(String series) {
        this.series = series;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }
}
