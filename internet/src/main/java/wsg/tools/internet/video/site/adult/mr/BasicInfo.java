package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;
import wsg.tools.internet.video.enums.Constellation;
import wsg.tools.internet.video.enums.Gender;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.site.adult.mr.enums.*;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Set;

/**
 * Basic information of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class BasicInfo {

    private Gender gender;
    private TemporalAccessor birthday;
    private String fullName;
    private List<String> zhNames;
    private List<String> jaNames;
    private List<String> enNames;
    private List<String> aka;
    private Zodiac zodiac;
    private Constellation constellation;
    private List<String> interests;
    private Integer height;
    private Integer weight;
    private CupEnum cup;
    private Figure figure;
    private BloodType bloodType;
    private List<Occupation> occupations;
    private Temporal start;
    private Temporal retire;
    private String agency;
    private String firm;
    private String school;
    private String birthplace;
    private Nation nation;
    private List<RegionEnum> nationalities;
    private List<LanguageEnum> languages;

    private Set<String> others;

    BasicInfo() {
    }

    void setGender(Gender gender) {
        this.gender = gender;
    }

    void setBirthday(TemporalAccessor birthday) {
        this.birthday = birthday;
    }

    void setFullName(String fullName) {
        this.fullName = fullName;
    }

    void setZhNames(List<String> zhNames) {
        this.zhNames = zhNames;
    }

    void setJaNames(List<String> jaNames) {
        this.jaNames = jaNames;
    }

    void setEnNames(List<String> enNames) {
        this.enNames = enNames;
    }

    void setAka(List<String> aka) {
        this.aka = aka;
    }

    void setZodiac(Zodiac zodiac) {
        this.zodiac = zodiac;
    }

    void setConstellation(Constellation constellation) {
        this.constellation = constellation;
    }

    void setInterests(List<String> interests) {
        this.interests = interests;
    }

    void setHeight(Integer height) {
        this.height = height;
    }

    void setWeight(Integer weight) {
        this.weight = weight;
    }

    void setCup(CupEnum cup) {
        this.cup = cup;
    }

    void setFigure(Figure figure) {
        this.figure = figure;
    }

    void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    void setOccupations(List<Occupation> occupations) {
        this.occupations = occupations;
    }

    void setStart(Temporal start) {
        this.start = start;
    }

    void setRetire(Temporal retire) {
        this.retire = retire;
    }

    void setAgency(String agency) {
        this.agency = agency;
    }

    void setFirm(String firm) {
        this.firm = firm;
    }

    void setSchool(String school) {
        this.school = school;
    }

    void setBirthplace(String birthplace) {
        this.birthplace = birthplace;
    }

    void setNation(Nation nation) {
        this.nation = nation;
    }

    void setNationalities(List<RegionEnum> nationalities) {
        this.nationalities = nationalities;
    }

    void setLanguages(List<LanguageEnum> languages) {
        this.languages = languages;
    }

    void setOthers(Set<String> others) {
        this.others = others;
    }
}
