package wsg.tools.internet.info.adult.wiki;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import wsg.tools.internet.enums.BloodType;
import wsg.tools.internet.enums.Constellation;
import wsg.tools.internet.enums.Gender;
import wsg.tools.internet.enums.Nation;
import wsg.tools.internet.enums.Zodiac;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * Basic information of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
class BasicInfo {

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
    private List<String> occupations;
    private Temporal start;
    private Temporal retire;
    private String agency;
    private String firm;
    private String school;
    private String birthplace;
    private Nation nation;

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
        this.zhNames = Collections.unmodifiableList(zhNames);
    }

    void setJaNames(List<String> jaNames) {
        this.jaNames = Collections.unmodifiableList(jaNames);
    }

    void setEnNames(List<String> enNames) {
        this.enNames = Collections.unmodifiableList(enNames);
    }

    void setAka(List<String> aka) {
        this.aka = Collections.unmodifiableList(aka);
    }

    void setZodiac(Zodiac zodiac) {
        this.zodiac = zodiac;
    }

    void setConstellation(Constellation constellation) {
        this.constellation = constellation;
    }

    void setInterests(List<String> interests) {
        this.interests = Collections.unmodifiableList(interests);
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

    void setOccupations(List<String> occupations) {
        this.occupations = Collections.unmodifiableList(occupations);
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

    void setOthers(Set<String> others) {
        this.others = Collections.unmodifiableSet(others);
    }
}
