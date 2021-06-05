package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.Duration;
import org.apache.commons.lang3.math.Fraction;
import wsg.tools.common.jackson.JsonDurationFormat;

/**
 * This class provides a skeleton implementation of the information about each media stream
 * contained in the input multimedia stream.
 *
 * @author Kingen
 * @since 2021/6/3
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "codec_type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = VideoStream.class, name = "video"),
    @JsonSubTypes.Type(value = AudioStream.class, name = "audio"),
    @JsonSubTypes.Type(value = SubtitleStream.class, name = "subtitle"),
    @JsonSubTypes.Type(value = AttachmentStream.class, name = "attachment"),
    @JsonSubTypes.Type(value = DataStream.class, name = "data"),
})
public abstract class AbstractStream {

    @JsonProperty(value = "index", required = true)
    private int index;

    @JsonProperty(value = "codec_name", required = true)
    private String codecName;

    @JsonProperty(value = "codec_long_name", required = true)
    private String codecLongName;

    @JsonProperty(value = "codec_tag_string", required = true)
    private String codecTagString;

    @JsonProperty(value = "codec_tag", required = true)
    private String codecTag;

    @JsonProperty(value = "r_frame_rate", required = true)
    private Fraction rFrameRate;

    @JsonProperty(value = "avg_frame_rate", required = true)
    private Fraction averageFrameRate;

    @JsonProperty(value = "time_base", required = true)
    private Fraction timeBase;

    @JsonProperty(value = "start_pts", required = true)
    private int startPts;

    @JsonProperty(value = "start_time", required = true)
    private double startTime;

    @JsonProperty(value = "disposition", required = true)
    private Disposition disposition;

    @JsonProperty("duration_ts")
    private Long durationTs;

    @JsonProperty("duration")
    @JsonDurationFormat(format = JsonDurationFormat.Format.DOUBLE)
    private Duration duration;

    AbstractStream() {
    }

    public int getIndex() {
        return index;
    }

    public String getCodecName() {
        return codecName;
    }

    public String getCodecLongName() {
        return codecLongName;
    }

    public String getCodecTagString() {
        return codecTagString;
    }

    public String getCodecTag() {
        return codecTag;
    }

    public Fraction getrFrameRate() {
        return rFrameRate;
    }

    public Fraction getAverageFrameRate() {
        return averageFrameRate;
    }

    public Fraction getTimeBase() {
        return timeBase;
    }

    public int getStartPts() {
        return startPts;
    }

    public double getStartTime() {
        return startTime;
    }

    public Disposition getDisposition() {
        return disposition;
    }

    public Long getDurationTs() {
        return durationTs;
    }

    public Duration getDuration() {
        return duration;
    }

    public static class Disposition {

        @JsonProperty(value = "dub", required = true)
        private int dub;

        @JsonProperty(value = "karaoke", required = true)
        private int karaoke;

        @JsonProperty(value = "default", required = true)
        private int jsonMemberDefault;

        @JsonProperty(value = "original", required = true)
        private int original;

        @JsonProperty(value = "visual_impaired", required = true)
        private int visualImpaired;

        @JsonProperty(value = "forced", required = true)
        private int forced;

        @JsonProperty(value = "attached_pic", required = true)
        private int attachedPic;

        @JsonProperty(value = "timed_thumbnails", required = true)
        private int timedThumbnails;

        @JsonProperty(value = "comment", required = true)
        private int comment;

        @JsonProperty(value = "hearing_impaired", required = true)
        private int hearingImpaired;

        @JsonProperty(value = "lyrics", required = true)
        private int lyrics;

        @JsonProperty(value = "clean_effects", required = true)
        private int cleanEffects;

        Disposition() {
        }

        public int getDub() {
            return dub;
        }

        public int getKaraoke() {
            return karaoke;
        }

        public int getJsonMemberDefault() {
            return jsonMemberDefault;
        }

        public int getOriginal() {
            return original;
        }

        public int getVisualImpaired() {
            return visualImpaired;
        }

        public int getForced() {
            return forced;
        }

        public int getAttachedPic() {
            return attachedPic;
        }

        public int getTimedThumbnails() {
            return timedThumbnails;
        }

        public int getComment() {
            return comment;
        }

        public int getHearingImpaired() {
            return hearingImpaired;
        }

        public int getLyrics() {
            return lyrics;
        }

        public int getCleanEffects() {
            return cleanEffects;
        }
    }
}