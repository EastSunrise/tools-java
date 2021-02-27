package wsg.tools.internet.video.site.douban.api.pojo;

import lombok.Getter;
import wsg.tools.internet.video.enums.Constellation;
import wsg.tools.internet.video.enums.Gender;

import java.time.LocalDate;
import java.util.List;

/**
 * A celebrity.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Getter
public class Celebrity extends SimpleCelebrity {

    private Gender gender;
    private List<String> aka;
    private List<String> akaEn;

    private List<Work> works;
    private List<String> professions;
    private List<Photo> photos;
    private String summary;
    private LocalDate birthday;
    private String bornPlace;
    private Constellation constellation;
    private String website;
    private String mobileUrl;

}
