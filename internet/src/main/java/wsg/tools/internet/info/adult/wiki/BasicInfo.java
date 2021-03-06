package wsg.tools.internet.info.adult.wiki;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import wsg.tools.internet.common.enums.BloodType;
import wsg.tools.internet.common.enums.Zodiac;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * Basic information of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class BasicInfo {

    private String gender;
    private String fullName;
    private List<String> zhNames;
    private List<String> jaNames;
    private List<String> enNames;
    private List<String> aka;
    private Zodiac zodiac;
    private String constellation;
    private List<String> interests;
    private Integer height;
    private Integer weight;
    private CupEnum cup;
    private BloodType bloodType;
    private List<String> occupations;
    private String agency;
    private String firm;
    private String school;
    private String birthplace;
    private String nation;

    private Set<String> others;

    BasicInfo() {
    }

    void setGender(String gender) {
        this.gender = gender;
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

    void setConstellation(String constellation) {
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

    void setBloodType(BloodType bloodType) {
        this.bloodType = bloodType;
    }

    void setOccupations(List<String> occupations) {
        if (occupations != null) {
            this.occupations = Collections.unmodifiableList(occupations);
        }
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

    void setNation(String nation) {
        this.nation = nation;
    }

    void setOthers(Set<String> others) {
        this.others = Collections.unmodifiableSet(others);
    }
}
