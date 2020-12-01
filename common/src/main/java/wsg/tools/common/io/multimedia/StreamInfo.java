package wsg.tools.common.io.multimedia;

import lombok.Getter;

/**
 * Info of stream within a multimedia.
 *
 * @author Kingen
 * @since 2020/12/1
 */
@Getter
public class StreamInfo extends MetadataInfo {

    private String type;
    private String content;

    void setType(String type) {
        this.type = type;
    }

    void setContent(String content) {
        this.content = content;
    }
}
