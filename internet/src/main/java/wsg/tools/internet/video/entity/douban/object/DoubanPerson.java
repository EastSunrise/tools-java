package wsg.tools.internet.video.entity.douban.object;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;

/**
 * A person.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Person")
public class DoubanPerson {

    /**
     * Combined with title and original title.
     */
    private String name;
    /**
     * Only path.
     */
    private String url;
}
