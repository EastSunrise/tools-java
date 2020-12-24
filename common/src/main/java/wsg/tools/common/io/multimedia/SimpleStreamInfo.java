package wsg.tools.common.io.multimedia;

import lombok.Getter;

/**
 * Info of a stream without details.
 *
 * @author Kingen
 * @since 2020/12/16
 */
@Getter
public class SimpleStreamInfo extends AbstractStreamInfo {
    private String type;
    private String content;

    void setType(String type) {
        this.type = type;
    }

    void setContent(String content) {
        this.content = content;
    }
}
