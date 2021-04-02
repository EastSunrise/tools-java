package wsg.tools.internet.movie.douban.api.pojo;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import wsg.tools.internet.common.enums.Constellation;
import wsg.tools.internet.common.enums.Gender;

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
