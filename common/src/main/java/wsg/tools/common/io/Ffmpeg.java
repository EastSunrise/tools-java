package wsg.tools.common.io;

import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.*;

import java.io.File;
import java.io.IOException;

/**
 * Utility for ffmpeg.
 *
 * @author Kingen
 * @since 2020/10/12
 */
@Slf4j
public final class Ffmpeg {

    /**
     * Common formats for {@link EncodingAttributes#setFormat(String)}.
     */
    public static final String ENCODER_FORMAT_ASS = "ass";
    public static final String ENCODER_FORMAT_AVI = "avi";
    public static final String ENCODER_FORMAT_F4V = "f4v";
    public static final String ENCODER_FORMAT_FLAC = "flac";
    public static final String ENCODER_FORMAT_FLV = "flv";
    public static final String ENCODER_FORMAT_GIF = "gif";
    public static final String ENCODER_FORMAT_H264 = "h264";
    public static final String ENCODER_FORMAT_M4V = "m4v";
    public static final String ENCODER_FORMAT_MATROSKA = "matroska";
    public static final String ENCODER_FORMAT_MOV = "mov";
    public static final String ENCODER_FORMAT_MP4 = "mp4";
    public static final String ENCODER_FORMAT_MPEG = "mpeg";
    public static final String ENCODER_FORMAT_MPEGTS = "mpegts";
    public static final String ENCODER_FORMAT_RM = "rm";
    public static final String ENCODER_FORMAT_SRT = "srt";
    public static final String ENCODER_FORMAT_WAV = "wav";

    /**
     * codecs for {@link VideoAttributes#setCodec(String)}.
     */
    public static final String VIDEO_CODEC_FLV = "flv";
    public static final String VIDEO_CODEC_GIF = "gif";
    public static final String VIDEO_CODEC_LIBX264 = "libx264";
    public static final String VIDEO_CODEC_LIBX265 = "libx265";

    private static final Encoder ENCODER = new Encoder();

    /**
     * Copy a video to another type.
     * <p>
     * Attention: this copy may lose some parts, like subtitle.
     */
    public static File copyVideo(File input, Filetype targetType, EncoderProgressListener listener) throws IOException, EncoderException {
        if (targetType == null || !targetType.isVideo()) {
            throw new IllegalArgumentException("Target type must be a type of video.");
        }
        if (Filetype.getRealType(input) == targetType) {
            throw new RuntimeException("Input file is already target type.");
        }

        if (listener == null) {
            listener = new DefaultListener();
        }
        File output = new File(input.getParent(), input.getName().substring(0, input.getName().lastIndexOf('.') + 1) + targetType.suffix());
        log.info("Copy from {} to {}.", input, output);
        ENCODER.encode(new MultimediaObject(input), output, defaultEncodingAttributes(), listener);
        return output;
    }

    /**
     * Cut a video file with 'copy' codec.
     *
     * @param output   it will be overwrote if the output file exists.
     * @param offset   may null
     * @param duration may null
     * @param listener use default one if null
     */
    public static void cutVideo(File input, File output, Float offset, Float duration, EncoderProgressListener listener) throws EncoderException {
        EncodingAttributes encodingAttributes = defaultEncodingAttributes();
        encodingAttributes.setOffset(offset);
        encodingAttributes.setDuration(duration);

        if (listener == null) {
            listener = new DefaultListener();
        }
        ENCODER.encode(new MultimediaObject(input), output, encodingAttributes, listener);
    }

    private static EncodingAttributes defaultEncodingAttributes() {
        EncodingAttributes encodingAttributes = new EncodingAttributes();
        VideoAttributes videoAttributes = new VideoAttributes();
        videoAttributes.setCodec(VideoAttributes.DIRECT_STREAM_COPY);
        encodingAttributes.setVideoAttributes(videoAttributes);
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
        encodingAttributes.setAudioAttributes(audioAttributes);
        return encodingAttributes;
    }
}
