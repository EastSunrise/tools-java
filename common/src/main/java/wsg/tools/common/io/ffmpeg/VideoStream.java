package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import org.apache.commons.lang3.math.Fraction;

/**
 * The video stream.
 *
 * @author Kingen
 * @since 2021/6/3
 */
public class VideoStream extends AbstractStream {

    @JsonProperty(value = "level", required = true)
    private int level;

    @JsonProperty(value = "pix_fmt", required = true)
    private String pixFormat;

    @JsonProperty(value = "width", required = true)
    private int width;

    @JsonProperty(value = "height", required = true)
    private int height;

    @JsonProperty(value = "coded_width", required = true)
    private int codedWidth;

    @JsonProperty(value = "coded_height", required = true)
    private int codedHeight;

    @JsonProperty(value = "closed_captions", required = true)
    private int closedCaptions;

    @JsonProperty(value = "has_b_frames", required = true)
    private int hasBFrames;

    @JsonProperty(value = "refs", required = true)
    private int refs;

    @JsonProperty("tags")
    private VideoTags tags;

    @JsonProperty("profile")
    private String profile;

    @JsonProperty("sample_aspect_ratio")
    private Fraction sampleAspectRatio;

    @JsonProperty("display_aspect_ratio")
    private Fraction displayAspectRatio;

    @JsonProperty("chroma_location")
    private String chromaLocation;

    @JsonProperty("bit_rate")
    private Long bitRate;

    @JsonProperty("field_order")
    private String fieldOrder;

    @JsonProperty("is_avc")
    private Boolean avc;

    @JsonProperty("nal_length_size")
    private Integer nalLengthSize;

    @JsonProperty("bits_per_raw_sample")
    private Integer bitsPerRawSample;

    @JsonProperty("color_range")
    private String colorRange;

    @JsonProperty("color_space")
    private String colorSpace;

    @JsonProperty("color_transfer")
    private String colorTransfer;

    @JsonProperty("color_primaries")
    private String colorPrimaries;

    @JsonProperty("nb_frames")
    private Integer nbFrames;

    VideoStream() {
    }

    public int getLevel() {
        return level;
    }

    public String getPixFormat() {
        return pixFormat;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getCodedWidth() {
        return codedWidth;
    }

    public int getCodedHeight() {
        return codedHeight;
    }

    public int getClosedCaptions() {
        return closedCaptions;
    }

    public int getHasBFrames() {
        return hasBFrames;
    }

    public int getRefs() {
        return refs;
    }

    public VideoTags getTags() {
        return tags;
    }

    public String getProfile() {
        return profile;
    }

    public Fraction getSampleAspectRatio() {
        return sampleAspectRatio;
    }

    public Fraction getDisplayAspectRatio() {
        return displayAspectRatio;
    }

    public String getChromaLocation() {
        return chromaLocation;
    }

    public Long getBitRate() {
        return bitRate;
    }

    public String getFieldOrder() {
        return fieldOrder;
    }

    public Boolean getAvc() {
        return avc;
    }

    public Integer getNalLengthSize() {
        return nalLengthSize;
    }

    public Integer getBitsPerRawSample() {
        return bitsPerRawSample;
    }

    public String getColorRange() {
        return colorRange;
    }

    public String getColorSpace() {
        return colorSpace;
    }

    public String getColorTransfer() {
        return colorTransfer;
    }

    public String getColorPrimaries() {
        return colorPrimaries;
    }

    public Integer getNbFrames() {
        return nbFrames;
    }

    public static class VideoTags extends BaseTags {

        @JsonProperty("filename")
        private String filename;

        @JsonProperty("creation_time")
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "utc")
        private LocalDateTime creationTime;

        @JsonProperty("encoder")
        private String encoder;

        @JsonProperty("handler_name")
        private String handlerName;

        @JsonProperty("vendor_id")
        private String vendorId;

        @JsonProperty("mimetype")
        private String mimetype;

        VideoTags() {
        }

        public String getFilename() {
            return filename;
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }

        public String getEncoder() {
            return encoder;
        }

        public String getHandlerName() {
            return handlerName;
        }

        public String getVendorId() {
            return vendorId;
        }

        public String getMimetype() {
            return mimetype;
        }
    }
}
