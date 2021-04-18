package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.internet.common.enums.Color;
import wsg.tools.internet.info.adult.common.CupEnum;
import wsg.tools.internet.info.adult.common.Measurements;

/**
 * A model in the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findModel(String)
 * @since 2021/3/15
 */
public class BabesModel implements BabesModelIndex {

    private final String namePath;
    private final String name;
    private final URL cover;
    private final int videos;
    private final int photos;

    private Integer height;
    private Integer weight;
    private Measurements measurements;
    private CupEnum cup;
    private Color hairColor;
    private Color eyeColor;
    private LocalDate birthday;
    private String birthplace;
    private String website;
    private List<BabesVideoIndex> videoIndices;

    BabesModel(String namePath, String name, URL cover, int videos, int photos) {
        this.namePath = namePath;
        this.name = name;
        this.cover = cover;
        this.videos = videos;
        this.photos = photos;
    }

    @Override
    public String getAsPath() {
        return namePath;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    @Override
    public int getVideos() {
        return videos;
    }

    @Override
    public int getPhotos() {
        return photos;
    }

    public Integer getHeight() {
        return height;
    }

    void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Measurements getMeasurements() {
        return measurements;
    }

    void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    public CupEnum getCup() {
        return cup;
    }

    void setCup(CupEnum cup) {
        this.cup = cup;
    }

    public Color getHairColor() {
        return hairColor;
    }

    void setHairColor(Color hairColor) {
        this.hairColor = hairColor;
    }

    public Color getEyeColor() {
        return eyeColor;
    }

    void setEyeColor(Color eyeColor) {
        this.eyeColor = eyeColor;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getBirthplace() {
        return birthplace;
    }

    void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    public String getWebsite() {
        return website;
    }

    void setWebsite(String website) {
        this.website = website;
    }

    public List<BabesVideoIndex> getVideoIndices() {
        return videoIndices;
    }

    void setVideoIndices(List<BabesVideoIndex> videoIndices) {
        this.videoIndices = videoIndices;
    }
}
