package wsg.tools.internet.resource.entity.rrys.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatus;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.time.Year;
import java.util.List;

/**
 * Items similar to the resource.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class SimilarItem extends ResourceItem {

    private Long rid;
    private Long recomId;
    private PlayStatus playStatus;
    @JoinedValue(separator = "/")
    private List<String> category;
    private Year publishYear;
    private Double score;
    private String poster;
    private String posterM;
    private String posterS;
    private String posterB;
    private String school;
}
