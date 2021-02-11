package wsg.tools.internet.video.site.douban.api.pojo;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.ConstellationEnum;
import wsg.tools.internet.video.enums.GenderEnum;

import java.time.LocalDate;
import java.util.List;

/**
 * A celebrity.
 *
 * @author Kingen
 * @since 2020/6/30
 */
@Setter
@Getter
public class Celebrity extends SimpleCelebrity {

    private GenderEnum gender;
    private List<String> aka;
    private List<String> akaEn;

    private List<Work> works;
    private List<String> professions;
    private List<Photo> photos;
    private String summary;
    private LocalDate birthday;
    private String bornPlace;
    private ConstellationEnum constellation;
    private String website;
    private String mobileUrl;

}
