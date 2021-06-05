package wsg.tools.common.io.ffmpeg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The stream of attached information.
 *
 * @author Kingen
 * @since 2021/6/3
 */
@JsonIgnoreProperties("side_data_list")
public class AttachmentStream extends AbstractStream {

    @JsonProperty(value = "tags", required = true)
    private AttachmentTags tags;

    AttachmentStream() {
    }

    public AttachmentTags getTags() {
        return tags;
    }

    public static class AttachmentTags {

        @JsonProperty(value = "filename", required = true)
        private String filename;

        @JsonProperty(value = "mimetype", required = true)
        private String mimetype;

        AttachmentTags() {
        }

        public String getFilename() {
            return filename;
        }

        public String getMimetype() {
            return mimetype;
        }
    }
}
