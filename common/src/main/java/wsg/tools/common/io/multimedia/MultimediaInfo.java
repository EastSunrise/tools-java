package wsg.tools.common.io.multimedia;

import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Info of a multimedia.
 *
 * @author Kingen
 * @since 2020/12/1
 */
@Getter
public class MultimediaInfo extends MetadataInfo {

    private String source;
    private String[] formats;

    private Duration duration;
    private Double start;
    private Integer bitrate;

    private List<VideoStreamInfo> videos;
    private List<AudioStreamInfo> audios;
    /**
     * Other streams
     */
    private List<SimpleStreamInfo> streams;
    private List<ChapterInfo> chapters;

    void addVideo(VideoStreamInfo videoStreamInfo) {
        if (videos == null) {
            videos = new ArrayList<>();
        }
        videos.add(videoStreamInfo);
    }

    void addAudio(AudioStreamInfo audioStreamInfo) {
        if (audios == null) {
            audios = new ArrayList<>();
        }
        audios.add(audioStreamInfo);
    }

    void addStream(SimpleStreamInfo streamInfo) {
        if (streams == null) {
            streams = new ArrayList<>();
        }
        streams.add(streamInfo);
    }

    void addChapter(ChapterInfo chapterInfo) {
        if (chapters == null) {
            chapters = new ArrayList<>();
        }
        chapters.add(chapterInfo);
    }

    void setSource(String source) {
        this.source = source;
    }

    void setFormats(String[] formats) {
        this.formats = formats;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    void setStart(Double start) {
        this.start = start;
    }

    void setBitrate(Integer bitrate) {
        this.bitrate = bitrate;
    }
}
