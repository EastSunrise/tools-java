package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * A book from Douban.
 *
 * @author Kingen
 * @since 2021/5/20
 */
public class DoubanBook extends AbstractSubject {

    @JsonProperty("workExample")
    private List<String> workExample;
    @JsonProperty("isbn")
    private String isbn;
    @JsonProperty("sameAs")
    private String sameAs;

    DoubanBook() {
    }

    @Override
    public DoubanCatalog getSubtype() {
        return DoubanCatalog.BOOK;
    }

    public List<String> getWorkExample() {
        return workExample;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getSameAs() {
        return sameAs;
    }
}
