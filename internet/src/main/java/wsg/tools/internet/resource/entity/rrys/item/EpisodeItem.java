package wsg.tools.internet.resource.entity.rrys.item;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.jackson.FileSizeDeserializer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * An item of an episode.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class EpisodeItem {

    private Long itemid;
    private String name;
    private Integer episode;

    /**
     * With KB as unit
     */
    @JsonDeserialize(using = FileSizeDeserializer.class)
    private Double size;
    private List<FileItem> files;

    private LocalDateTime dateline;
    private int yyetsTrans;
}
