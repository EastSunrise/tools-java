package wsg.tools.internet.movie.douban.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;

/**
 * A person.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Person")
public class DoubanPerson {

    /**
     * Only path.
     */
    @JsonProperty("url")
    private String url;
    /**
     * Combined with title and original title.
     */
    @JsonProperty("name")
    private String name;
}
