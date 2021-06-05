package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.Fraction;
import wsg.tools.common.constant.Constants;
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

    public FfprobeExecutor(String executablePath) {
        super(executablePath);
    }

    /**
     * Show information about the container format of the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return the format
     * @throws IOException if an I/O error occurs
     */
    public Format showFormat(String path) throws IOException {
        return getResult(path, "-show_format").format;
    }

    /**
     * Shows information about each media stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of streams
     * @throws IOException if an I/O error occurs
     */
    public List<AbstractStream> showStreams(String path) throws IOException {
        return getResult(path, "-show_streams").streams;
    }

    /**
     * Shows information about each video stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of video streams
     * @throws IOException if an I/O error occurs
     */
    public List<VideoStream> showVideoStreams(String path) throws IOException {
        return showStreams(path, "v", VideoStream.class);
    }

    /**
     * Shows information about each audio stream contained in the input multimedia stream.
     *
     * @param path path of the input multimedia stream
     * @return list of audio streams
     * @throws IOException if an I/O error occurs
     */
    public List<AudioStream> showAudioStreams(String path) throws IOException {
        return showStreams(path, "a", AudioStream.class);
    }

    private <T extends AbstractStream> List<T>
    showStreams(String path, String arg, Class<T> clazz) throws IOException {
        return getResult(path, "-show_streams", "-select_streams", arg).streams
            .stream().map(clazz::cast).collect(Collectors.toList());
    }

    private Result getResult(String path, String... args) throws IOException {
        path = "\"" + path + "\"";
        String[] commands = ArrayUtils.addAll(args, "-v", "quiet", "-of", "json", "-i", path);
        CommandTask task = createTask(commands);
        task.execute();
        try {
            // todo other charsets
            String json = IOUtils.toString(task.getInputStream(), Constants.UTF_8);
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
    }
}
