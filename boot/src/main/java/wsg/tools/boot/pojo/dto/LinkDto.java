package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.ResourceType;

import java.util.Objects;

/**
 * Object of a link.
 *
 * @author Kingen
 * @since 2020/11/28
 */
@Getter
@Setter
public class LinkDto extends BaseDto {
    private ResourceType type;
    private String url;
    private String title;
    private String filename;
    private Long length;
    private String thunder;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LinkDto linkDto = (LinkDto) o;
        return Objects.equals(url, linkDto.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
