package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The subtitle stream.
 *
 * @author Kingen
 * @since 2021/6/3
 */
public class SubtitleStream extends AbstractStream {

    @JsonProperty("width")
    private Integer width;

    @JsonProperty("height")
    private Integer height;

    @JsonProperty("tags")
    private BaseTags tags;

    SubtitleStream() {
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }

    public BaseTags getTags() {
        return tags;
    }
}
