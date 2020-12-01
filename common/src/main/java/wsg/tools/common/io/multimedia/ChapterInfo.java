package wsg.tools.common.io.multimedia;

import lombok.Getter;

/**
 * Info of a chapter within a multimedia.
 *
 * @author Kingen
 * @since 2020/12/2
 */
@Getter
public class ChapterInfo extends MetadataInfo {

    private double start;
    private double end;

    void setStart(double start) {
        this.start = start;
    }

    void setEnd(double end) {
        this.end = end;
    }
}
