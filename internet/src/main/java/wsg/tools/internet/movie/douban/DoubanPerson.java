package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * A person from Douban, an author, an actor or else.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonTypeName("Person")
public class DoubanPerson {

    @JsonProperty("url")
    private String url;

    @JsonProperty("name")
    private String name;

    DoubanPerson() {
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
