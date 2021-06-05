package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * The audio stream.
 *
 * @author Kingen
 * @since 2021/6/3
 */
@JsonIgnoreProperties("side_data_list")
public class AudioStream extends AbstractStream {

    @JsonProperty("profile")
    private String profile;

    @JsonProperty(value = "sample_fmt", required = true)
    private String sampleFormat;

    @JsonProperty(value = "sample_rate", required = true)
    private int sampleRate;

    @JsonProperty(value = "channels", required = true)
    private int channels;

    @JsonProperty(value = "channel_layout", required = true)
    private String channelLayout;

    @JsonProperty("bit_rate")
    private Long bitRate;

    @JsonProperty(value = "bits_per_sample", required = true)
    private int bitsPerSample;

    @JsonProperty("bits_per_raw_sample")
    private Integer bitsPerRawSample;

    @JsonProperty("tags")
    private AudioTags tags;

    @JsonProperty("nb_frames")
    private Integer nbFrames;

    @JsonProperty("dmix_mode")
    private Integer dmixMode;

    @JsonProperty("ltrt_cmixlev")
    private Double ltrtCmixlev;

    @JsonProperty("ltrt_surmixlev")
    private Double ltrtSurmixlev;

    @JsonProperty("loro_cmixlev")
    private Double loroCmixlev;

    @JsonProperty("loro_surmixlev")
    private Double loroSurmixlev;

    AudioStream() {
    }

    public String getProfile() {
        return profile;
    }

    public String getSampleFormat() {
        return sampleFormat;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public int getChannels() {
        return channels;
    }

    public String getChannelLayout() {
        return channelLayout;
    }

    public Long getBitRate() {
        return bitRate;
    }

    public int getBitsPerSample() {
        return bitsPerSample;
    }

    public Integer getBitsPerRawSample() {
        return bitsPerRawSample;
    }

    public AudioTags getTags() {
        return tags;
    }

    public Integer getNbFrames() {
        return nbFrames;
    }

    public Integer getDmixMode() {
        return dmixMode;
    }

    public Double getLtrtCmixlev() {
        return ltrtCmixlev;
    }

    public Double getLtrtSurmixlev() {
        return ltrtSurmixlev;
    }

    public Double getLoroCmixlev() {
        return loroCmixlev;
    }

    public Double getLoroSurmixlev() {
        return loroSurmixlev;
    }

    public static class AudioTags extends BaseTags {

        @JsonProperty("creation_time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "utc")
        private LocalDateTime creationTime;

        @JsonProperty("handler_name")
        private String handlerName;

        @JsonProperty("vendor_id")
        private String vendorId;

        AudioTags() {
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }

        public String getHandlerName() {
            return handlerName;
        }

        public String getVendorId() {
            return vendorId;
        }
    }
}
