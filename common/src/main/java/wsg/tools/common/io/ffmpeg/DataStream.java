package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * The data stream.
 *
 * @author Kingen
 * @since 2021/6/4
 */
public class DataStream extends AbstractStream {

    @JsonProperty(value = "nb_frames", required = true)
    private int nbFrames;

    @JsonProperty(value = "tags", required = true)
    private DataTags tags;

    @JsonProperty("bit_rate")
    private Long bitRate;

    DataStream() {
    }

    public int getNbFrames() {
        return nbFrames;
    }

    public DataTags getTags() {
        return tags;
    }

    public Long getBitRate() {
        return bitRate;
    }

    public static class DataTags {

        @JsonProperty("language")
        private String language;

        @JsonProperty("handler_name")
        private String handlerName;

        @JsonProperty("creation_time")
        private LocalDateTime creationTime;

        DataTags() {
        }

        public String getLanguage() {
            return language;
        }

        public String getHandlerName() {
            return handlerName;
        }

        public LocalDateTime getCreationTime() {
            return creationTime;
        }
    }
}
