package wsg.tools.common.io.multimedia;

import lombok.Getter;

/**
 * Info of video stream in a multimedia.
 *
 * @author Kingen
 * @since 2020/12/6
 */
@Getter
public class VideoStreamInfo extends AbstractStreamInfo {

    public static final String TYPE = "Video";

    private String decoder;
    private Integer frameWidth;
    private Integer frameHeight;
    private Integer bitrate;
    private Double frameRate;

    void setDecoder(String decoder) {
        this.decoder = decoder;
    }

    void setFrameWidth(Integer frameWidth) {
        this.frameWidth = frameWidth;
    }

    void setFrameHeight(Integer frameHeight) {
        this.frameHeight = frameHeight;
    }

    void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    void setFrameRate(Double frameRate) {
        this.frameRate = frameRate;
    }
}
