package wsg.tools.common.io.multimedia;

import lombok.Getter;

/**
 * Info of audio stream in a multimedia.
 *
 * @author Kingen
 * @since 2020/12/6
 */
@Getter
public class AudioStreamInfo extends AbstractStreamInfo {

    public static final String TYPE = "Audio";

    private String decoder;
    private Integer samplingRate;
    private AudioChannel channel;
    private Integer bitrate;

    void setDecoder(String decoder) {
        this.decoder = decoder;
    }

    void setSamplingRate(Integer samplingRate) {
        this.samplingRate = samplingRate;
    }

    void setChannel(AudioChannel channel) {
        this.channel = channel;
    }

    void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }

    public enum AudioChannel {
        /**
         * Channels
         */
        MONO,
        STEREO,
        QUAD
    }
}
