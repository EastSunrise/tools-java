package wsg.tools.common.io.multimedia;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.io.CommandExecutor;
import wsg.tools.common.io.CommandTask;
import wsg.tools.common.util.regex.RegexUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A ffmpeg process wrapper.
 *
 * @author Kingen
 * @since 2020/12/1
 */
@Slf4j
public class FfmpegExecutor {

    private static final Pattern INPUT_REGEX = Pattern.compile("Input #(?<i>\\d+), (?<fs>[a-z0-9]+(,[a-z0-9]+)*), from '(?<src>.*)':");
    private static final Pattern DURATION_REGEX =
            Pattern.compile("Duration: (?<h>\\d{2}):(?<min>\\d{2}):(?<s>\\d{2})\\.(?<ms>\\d{2}), " +
                    "start: (?<start>\\d+\\.\\d+), " +
                    "bitrate: (?<br>\\d+) kb/s");
    private static final Pattern STREAM_REGEX =
            Pattern.compile("Stream #(?<i>\\d+):(?<j>\\d+)(\\((?<remark>[a-z]+)\\))?: (?<type>Audio|Video|Subtitle|Attachment): (?<c>.*)");
    private static final Pattern CHAPTER_REGEX =
            Pattern.compile("Chapter #(?<i>\\d+):(?<j>\\d+): start (?<start>\\d+\\.\\d+), end (?<end>\\d+\\.\\d+)");
    private static final Pattern FRAME_SIZE_REGEX = Pattern.compile("(?<w>\\d+)x(?<h>\\d+)");
    private static final Pattern FRAME_RATE_REGEX = Pattern.compile("(?<r>[\\d.]+)\\s+fps");
    private static final Pattern BIT_RATE_REGEX = Pattern.compile("(?<r>\\d+)\\s+kb/s");
    private static final Pattern SAMPLING_RATE_REGEX = Pattern.compile("(?<r>\\d+)\\s+Hz");
    private static final Pattern CHANNEL_REGEX = Pattern.compile("(?<c>mono|stereo|quad)", Pattern.CASE_INSENSITIVE);

    private final CommandExecutor ffmpeg;
    private final CommandExecutor ffprobe;
    private final CommandExecutor ffplay;

    /**
     * Constructs a ffmpeg executor with given bin directory of ffmpeg.
     */
    public FfmpegExecutor(String binDir) {
        this.ffmpeg = new CommandExecutor(binDir + File.separator + "ffmpeg");
        this.ffprobe = new CommandExecutor(binDir + File.separator + "ffprobe");
        this.ffplay = new CommandExecutor(binDir + File.separator + "ffplay");
    }

    public MultimediaInfo getInfo(URL url) throws ParseException, InputFormatException, IOException {
        return getInfo(url.toString());
    }

    public MultimediaInfo getInfo(File file) throws ParseException, InputFormatException, IOException {
        return getInfo(file.getAbsolutePath());
    }

    public MultimediaInfo getInfo(String target) throws InputFormatException, ParseException, IOException {
        CommandTask task = ffprobe.createTask("-i", "\"" + target + "\"");
        task.execute();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(task.getErrorStream()));
            return parseMultimediaInfo(target, reader).get(0);
        } finally {
            task.destroy();
        }
    }

    /**
     * Parses the ffprobe output, extracting information of the input target.
     *
     * @param target path to the resource.
     * @param reader ffprobe output channel.
     * @return information of the target
     * @throws IOException          If a problem occurs when reading the stream.
     * @throws InputFormatException if the format of the source file cannot be recognized and decoded.
     * @throws ParseException       If stream of the info cannot be recognized
     */
    private List<MultimediaInfo> parseMultimediaInfo(String target, BufferedReader reader) throws InputFormatException, ParseException, IOException {
        List<String> lines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        List<MultimediaInfo> infos = new ArrayList<>();
        int index = 0;
        while (index < lines.size()) {
            line = lines.get(index);
            if (line.startsWith("ffprobe version")) {
                index = readVersion(lines, index + 1);
                continue;
            }
            if (line.startsWith(target)) {
                throw new InputFormatException(line);
            }
            if (line.startsWith("Input")) {
                MultimediaInfo info = new MultimediaInfo();
                Matcher matcher = RegexUtils.matchesOrElseThrow(INPUT_REGEX, line);
                info.setSource(matcher.group("src"));
                info.setFormats(matcher.group("fs").split(","));
                index = readInput(lines, index + 1, info);
                infos.add(info);
                continue;
            }
            log.warn("Unknown part: " + line);
            index++;
        }
        return infos;
    }

    private int readVersion(List<String> lines, int index) {
        while (index < lines.size()) {
            String line = lines.get(index);
            int current = StringUtils.indexOfAnyBut(line, ' ');
            if (current == 0) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * Reads info of an input source.
     *
     * @return start index of next block
     */
    private int readInput(List<String> lines, int index, MultimediaInfo info) throws ParseException {
        MetadataInfo currentObject = info;
        while (index < lines.size()) {
            String line = lines.get(index);
            int current = StringUtils.indexOfAnyBut(line, ' ');
            if (current == 0) {
                return index;
            }

            line = line.substring(current);
            if (current == 2) {
                if (line.startsWith("Metadata")) {
                    Map<String, String> metadata = new HashMap<>(4);
                    index = readMap(lines, index + 1, metadata, current);
                    currentObject.setMetadata(metadata);
                    continue;
                }
                if (line.startsWith("Duration")) {
                    Matcher matcher = RegexUtils.matchesOrElseThrow(DURATION_REGEX, line);
                    long h = Long.parseLong(matcher.group("h"));
                    long min = Long.parseLong(matcher.group("min"));
                    long s = Long.parseLong(matcher.group("s"));
                    long ms = Long.parseLong(matcher.group("ms")) * 10;
                    info.setDuration(Duration.ofHours(h).plusMinutes(min).plusSeconds(s).plusMillis(ms));
                    info.setStart(Double.parseDouble(matcher.group("start")));
                    info.setBitrate(Integer.parseInt(matcher.group("br")));
                    index++;
                    continue;
                }
            }
            if (current == 4) {
                if (line.startsWith("Stream")) {
                    StreamInfo streamInfo = parseStreamSpecs(line);
                    info.addStream(streamInfo);
                    currentObject = streamInfo;
                    index++;
                    continue;
                }
                if (line.startsWith("Chapter")) {
                    Matcher matcher = RegexUtils.matchesOrElseThrow(CHAPTER_REGEX, line);
                    ChapterInfo chapterInfo = new ChapterInfo();
                    chapterInfo.setStart(Double.parseDouble(matcher.group("start")));
                    chapterInfo.setEnd(Double.parseDouble(matcher.group("end")));
                    info.addChapter(chapterInfo);
                    currentObject = chapterInfo;
                    index++;
                    continue;
                }
                // metadata of last stream
                if (line.startsWith("Metadata")) {
                    Map<String, String> metadata = new HashMap<>(4);
                    index = readMap(lines, index + 1, metadata, current);
                    currentObject.setMetadata(metadata);
                    continue;
                }
                if (line.startsWith("Side data")) {
                    // ignore
                    Map<String, String> sideData = new HashMap<>(4);
                    index = readMap(lines, index + 1, sideData, current);
                    continue;
                }
            }
            throw new ParseException("Unknown line of input: " + line);
        }
        return index;
    }

    private StreamInfo parseStreamSpecs(String line) {
        Matcher matcher = RegexUtils.matchesOrElseThrow(STREAM_REGEX, line);
        String type = matcher.group("type");
        String content = matcher.group("c");
        if (VideoStreamInfo.TYPE.equalsIgnoreCase(type)) {
            VideoStreamInfo videoStreamInfo = new VideoStreamInfo();
            StringTokenizer tokenizer = new StringTokenizer(content, ",");
            if (tokenizer.hasMoreTokens()) {
                videoStreamInfo.setDecoder(tokenizer.nextToken().trim());
            }
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().strip();
                Matcher m = FRAME_SIZE_REGEX.matcher(token);
                if (m.find()) {
                    videoStreamInfo.setFrameWidth(Integer.parseInt(m.group("w")));
                    videoStreamInfo.setFrameHeight(Integer.parseInt(m.group("h")));
                    continue;
                }
                Matcher m2 = FRAME_RATE_REGEX.matcher(token);
                if (m2.find()) {
                    videoStreamInfo.setFrameRate(Double.parseDouble(m2.group("r")));
                    continue;
                }
                Matcher m3 = BIT_RATE_REGEX.matcher(token);
                if (m3.find()) {
                    videoStreamInfo.setBitrate(Integer.parseInt(m3.group("r")));
                }
            }
            return videoStreamInfo;
        }
        if (AudioStreamInfo.TYPE.equalsIgnoreCase(type)) {
            AudioStreamInfo audioStreamInfo = new AudioStreamInfo();
            StringTokenizer tokenizer = new StringTokenizer(content, ",");
            if (tokenizer.hasMoreTokens()) {
                audioStreamInfo.setDecoder(tokenizer.nextToken().trim());
            }
            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken().strip();
                Matcher m = SAMPLING_RATE_REGEX.matcher(token);
                if (m.find()) {
                    audioStreamInfo.setSamplingRate(Integer.parseInt(m.group("r")));
                    continue;
                }
                Matcher m2 = CHANNEL_REGEX.matcher(token);
                if (m2.find()) {
                    audioStreamInfo.setChannel(EnumUtils.getEnumIgnoreCase(AudioStreamInfo.AudioChannel.class, m2.group("c")));
                    continue;
                }
                Matcher m3 = BIT_RATE_REGEX.matcher(token);
                if (m3.find()) {
                    audioStreamInfo.setBitrate(Integer.parseInt(m3.group("r")));
                }
            }
            return audioStreamInfo;
        }
        StreamInfo streamInfo = new StreamInfo();
        streamInfo.setType(type);
        streamInfo.setContent(content);
        return streamInfo;
    }

    /**
     * Reads mapped data of a block.
     *
     * @param index start index of the mapped data
     * @param map   map to store data
     * @return start index of next block
     */
    private int readMap(List<String> lines, int index, @NonNull Map<String, String> map, int parent) throws ParseException {
        while (index < lines.size()) {
            String line = lines.get(index);
            int current = StringUtils.indexOfAnyBut(line, ' ');
            if (current != parent + 2) {
                break;
            }
            int indexOfSeparator = line.indexOf(": ");
            if (indexOfSeparator < 0) {
                throw new ParseException("Unknown metadata: " + line);
            }
            map.put(line.substring(0, indexOfSeparator).strip(), line.substring(indexOfSeparator + 1).strip());
            index++;
        }
        return index;
    }
}
