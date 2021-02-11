package wsg.tools.boot.pojo.dto;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import java.util.List;

/**
 * Subject of movie.
 *
 * @author Kingen
 * @since 2020/12/4
 */
@Getter
@Setter
public class MovieDto extends SubjectDto implements ImdbIdentifier {

    private String imdbId;
    private List<LanguageEnum> languages;
}
