package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;

/**
 * A subject from Douban, a book, a movie or else.
 *
 * @author Kingen
 * @since 2021/5/20
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
    @JsonSubTypes.Type(value = DoubanBook.class, name = "Book"),
    @JsonSubTypes.Type(value = DoubanMovie.class, name = "Movie"),
    @JsonSubTypes.Type(value = DoubanSeries.class, name = "TVSeries")
})
@JsonIgnoreProperties("@context")
abstract class AbstractSubject implements SubjectIndex {

    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("url")
    private String url;
    @JsonProperty("author")
    private List<DoubanPerson> authors;

    AbstractSubject() {
    }

    @Override
    public Long getDbId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public List<DoubanPerson> getAuthors() {
        return authors;
    }
}
