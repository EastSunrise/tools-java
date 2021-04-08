package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.internet.info.adult.view.FormalJaAdultEntry;
import wsg.tools.internet.info.adult.view.Tagged;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * An adult entry of a celebrity.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findAdultEntry(String)
 * @since 2021/2/24
 */
public class WikiAdultEntry implements TitledAdultEntry, FormalJaAdultEntry, Tagged {

    private final WikiSimpleCelebrity celebrity;
    private final String serialNum;
    private final String title;
    private final URL cover;
    private final List<String> actresses;
    private boolean mosaic;
    private Duration duration;
    private LocalDate release;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private String[] tags;

    WikiAdultEntry(WikiSimpleCelebrity celebrity, String serialNum, String title,
        URL cover, List<String> actresses) {
        this.celebrity = celebrity;
        this.serialNum = serialNum;
        this.title = title;
        this.cover = cover;
        this.actresses = actresses;
    }

    public WikiSimpleCelebrity getCelebrity() {
        return celebrity;
    }

    @Override
    public String getSerialNum() {
        return serialNum;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    @Override
    public List<String> getActresses() {
        return actresses;
    }

    @Override
    public Boolean getMosaic() {
        return mosaic;
    }

    void setMosaic(Boolean mosaic) {
        this.mosaic = mosaic;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public LocalDate getRelease() {
        return release;
    }

    void setRelease(LocalDate release) {
        this.release = release;
    }

    public String getDirector() {
        return director;
    }

    void setDirector(String director) {
        this.director = director;
    }

    @Override
    public String getProducer() {
        return producer;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    @Override
    public String getDistributor() {
        return distributor;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    @Override
    public String getSeries() {
        return series;
    }

    void setSeries(String series) {
        this.series = series;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
