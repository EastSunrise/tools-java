package wsg.tools.common.io;

import lombok.extern.slf4j.Slf4j;
import ws.schild.jave.*;

import java.io.File;

/**
 * Handle files.
 *
 * @author Kingen
 * @since 2020/10/12
 */
@Slf4j
public final class FileHandler {

    private static final Encoder ENCODER = new Encoder();

    /**
     * Cut a video file with 'copy' codec.
     *
     * @param output   it will be overwrote if the output file exists.
     * @param offset   may null
     * @param duration may null
     * @param listener use default one if null
     */
    public static void cutVideo(File input, File output, Float offset, Float duration, EncoderProgressListener listener) throws EncoderException {
        EncodingAttributes encodingAttributes = new EncodingAttributes();
        encodingAttributes.setOffset(offset);
        encodingAttributes.setDuration(duration);
        VideoAttributes videoAttributes = new VideoAttributes();
        videoAttributes.setCodec(VideoAttributes.DIRECT_STREAM_COPY);
        encodingAttributes.setVideoAttributes(videoAttributes);
        AudioAttributes audioAttributes = new AudioAttributes();
        audioAttributes.setCodec(AudioAttributes.DIRECT_STREAM_COPY);
        encodingAttributes.setAudioAttributes(audioAttributes);

        if (listener == null) {
            listener = new DefaultListener();
        }
        ENCODER.encode(new MultimediaObject(input), output, encodingAttributes, listener);
    }
}
