package wsg.tools.internet.resource.entity.rrys.item;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.common.FileWayEnum;

/**
 * An item of a file.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
@JsonIgnoreProperties("way_cn")
public class FileItem {

    private String address;
    private String passwd;
    private FileWayEnum way;
}
