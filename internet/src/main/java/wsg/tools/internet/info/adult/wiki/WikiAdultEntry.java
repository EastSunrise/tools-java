package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.ActressSupplier;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.SerialNumSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * An adult entry of a celebrity.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findAdultEntry(String)
 * @since 2021/2/24
 */
public class WikiAdultEntry implements SerialNumSupplier, TitleSupplier,
    CoverSupplier, DurationSupplier, Tagged, ActressSupplier {

    private final WikiSimpleCelebrity celebrity;
    private final String serialNum;
    private final String title;
    private final URL cover;
    private final List<String> actresses;
    private boolean mosaic;
    private Duration duration;
    private LocalDate publish;
    private String director;
    private String producer;
    private String distributor;
    private String series;
    private Set<String> tags = new HashSet<>(0);

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
    public URL getCoverURL() {
        return cover;
    }

    @Nonnull
    @Override
    public List<String> getActresses() {
        return actresses;
    }

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

    public LocalDate getPublish() {
        return publish;
    }

    void setPublish(LocalDate publish) {
        this.publish = publish;
    }

    public String getDirector() {
        return director;
    }

    void setDirector(String director) {
        this.director = director;
    }

    public String getProducer() {
        return producer;
    }

    void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDistributor() {
        return distributor;
    }

    void setDistributor(String distributor) {
        this.distributor = distributor;
    }

    public String getSeries() {
        return series;
    }

    void setSeries(String series) {
        this.series = series;
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return tags;
    }

    void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
