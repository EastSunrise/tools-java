package wsg.tools.internet.info.adult;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import wsg.tools.internet.enums.Color;
import wsg.tools.internet.info.adult.common.Measurements;

/**
 * A model in the {@link BabesTubeSite}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesModel extends BabesModelIndex {

    private Integer height;
    private Integer weight;
    private Measurements measurements;
    private Color hairColor;
    private Color eyeColor;
    private LocalDate birthday;
    private String birthplace;
    private String website;
    private List<BabesVideoIndex> videoIndices;

    BabesModel(String id, String name, URL cover, int videos, int photos) {
        super(id, name, cover, videos, photos);
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
