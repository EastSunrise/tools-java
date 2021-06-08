package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.Fraction;
import wsg.tools.common.Constants;
import wsg.tools.common.io.CommandExecutor;
import wsg.tools.common.io.CommandTask;

/**
 * The executor of ffprobe that gathers information from multimedia streams and prints it in human-
 * and machine-readable fashion.
 *
 * @author Kingen
 * @see <a href="http://www.ffmpeg.org/ffprobe.html">ffprobe Documentation</a>
 * @since 2021/6/3
 */
public class FfprobeExecutor extends CommandExecutor {

    private final Charset charset;

    public FfprobeExecutor(String executablePath) {
        this(executablePath, Constants.UTF_8);
    }

    public FfprobeExecutor(String executablePath, Charset charset) {
        super(executablePath);
        this.charset = Objects.requireNonNull(charset);
    }

    /**
     * Show information about the container format of the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return the format
     * @throws IOException if an I/O error occurs
     */
    public Format showFormat(String path) throws IOException {
        return this.getResult(path, "-show_format").format;
    }

    /**
     * Shows information about each media stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of streams
     * @throws IOException if an I/O error occurs
     */
    public List<AbstractStream> showStreams(String path) throws IOException {
        return this.getResult(path, "-show_streams").streams;
    }

    /**
     * Shows information about each video stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of video streams
     * @throws IOException if an I/O error occurs
     */
    public List<VideoStream> showVideoStreams(String path) throws IOException {
        return this.showStreams(path, "v", VideoStream.class);
    }

    /**
     * Shows information about each audio stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of audio streams
     * @throws IOException if an I/O error occurs
     */
    public List<AudioStream> showAudioStreams(String path) throws IOException {
        return this.showStreams(path, "a", AudioStream.class);
    }

    private <T extends AbstractStream> List<T>
    showStreams(String path, String arg, Class<T> clazz) throws IOException {
        return this.getResult(path, "-show_streams", "-select_streams", arg).streams
            .stream().map(clazz::cast).collect(Collectors.toList());
    }

    private Result getResult(String path, String... args) throws IOException {
        String[] commands = ArrayUtils
            .addAll(args, "-v", "quiet", "-of", "json", "-i", "\"" + path + "\"");
        CommandTask task = this.createTask(commands);
        task.execute();
        try {
            String json = IOUtils.toString(task.getInputStream(), charset);
            return Lazy.MAPPER.readValue(json, Result.class);
        } finally {
            task.destroy();
        }
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new SimpleModule()
                .addDeserializer(Fraction.class, new FractionDeserializer()))
            .registerModule(new JavaTimeModule());
    }

    private static class Result {

        @JsonProperty("streams")
        private List<AbstractStream> streams;
        @JsonProperty("format")
        private Format format;

        Result() {
        }

        private List<AbstractStream> getStreams() {
            return streams;
        }

        private Format getFormat() {
            return format;
        }
    }
}
