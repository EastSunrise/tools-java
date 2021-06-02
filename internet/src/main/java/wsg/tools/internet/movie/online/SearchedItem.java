package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URL;
import java.util.List;
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.enums.Region;
import wsg.tools.internet.common.jackson.JsonJoinedValue;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * Searched items.
 *
 * @author Kingen
 * @since 2021/5/30
 */
public class SearchedItem implements CoverSupplier {

    @JsonProperty("id")
    private int id;

    @JsonProperty("title")
    private String title;

    @JsonProperty("classify")
    private Classification classify;

    @JsonProperty("status")
    private SeriesStatus status;

    @JsonProperty("year")
    private int year;

    @JsonJoinedValue(separator = "/")
    @JsonProperty("area")
    private List<Region> areas;

    @JsonJoinedValue(separator = "/")
    @JsonProperty("cat")
    private List<MovieGenre> genres;

    @JsonProperty("brief")
    private String description;

    @JsonProperty("cover")
    private String cover;

    @JsonProperty("director")
    private String director;

    @JsonProperty("actor")
    private String actor;

    @JsonProperty("score")
    private Double score;

    @JsonProperty("view_count")
    private long viewCount;

    @JsonProperty("highlights")
    private Object highlights;

    @JsonProperty("upInfo")
    private String upInfo;

    @JsonProperty("sort")
    private double sort;

    @JsonProperty("search_after")
    private String searchAfter;

    SearchedItem() {
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Classification getClassify() {
        return classify;
    }

    public SeriesStatus getStatus() {
        return status;
    }

    public int getYear() {
        return year;
    }

    public List<Region> getAreas() {
        return areas;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public String getDescription() {
        return description;
    }

    public String getCover() {
        return cover;
    }

    public String getDirector() {
        return director;
    }

    public String getActor() {
        return actor;
    }

    public Double getScore() {
        return score;
    }

    public long getViewCount() {
        return viewCount;
    }

    public Object getHighlights() {
        return highlights;
    }

    public String getUpInfo() {
        return upInfo;
    }

    public double getSort() {
        return sort;
    }

    public String getSearchAfter() {
        return searchAfter;
    }

    @Override
    public URL getCoverURL() {
        return NetUtils.createURL(cover);
    }
}
