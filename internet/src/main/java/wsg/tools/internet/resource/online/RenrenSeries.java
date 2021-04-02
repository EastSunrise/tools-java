package wsg.tools.internet.resource.online;

import java.util.List;
import wsg.tools.internet.enums.Region;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * A series on the site.
 *
 * @author Kingen
 * @since 2021/4/2
 */
public class RenrenSeries {

    private final int id;
    private final String title;
    private final String description;
    private final double score;
    private final int year;
    private final List<Region> regions;
    private final List<MovieGenre> genres;
    private final int episodes;

    private SeriesStatus status;

    RenrenSeries(int id, String title, String description, double score, int year,
        List<Region> regions, List<MovieGenre> genres, int episodes) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.score = score;
        this.year = year;
        this.regions = regions;
        this.genres = genres;
        this.episodes = episodes;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getScore() {
        return score;
    }

    public int getYear() {
        return year;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public int getEpisodes() {
        return episodes;
    }

    public SeriesStatus getStatus() {
        return status;
    }

    void setStatus(SeriesStatus status) {
        this.status = status;
    }
}
