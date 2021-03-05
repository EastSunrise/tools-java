package wsg.tools.boot.pojo.dto;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Subject of movie.
 *
 * @author Kingen
 * @since 2020/12/4
 */
@Getter
@Setter
public class MovieDto extends SubjectDto implements ImdbIdentifier {

    private static final long serialVersionUID = 5774350479644647488L;

    private String originalTitle;
    private String imdbId;
    private List<Language> languages;

    public static MovieDto fromEntity(MovieEntity entity) {
        MovieDto movie = new MovieDto();
        movie.setId(entity.getId());
        movie.setImdbId(entity.getImdbId());
        movie.setZhTitle(entity.getZhTitle());
        movie.setOriginalTitle(entity.getOriginalTitle());
        movie.setYear(entity.getYear());
        movie.setLanguages(entity.getLanguages());
        movie.setDbId(entity.getDbId());
        movie.setDurations(
            entity.getDurations().stream().map(Duration::toMinutes).map(String::valueOf)
                .collect(Collectors.joining("/")));
        movie.setGmtModified(entity.getGmtModified());
        return movie;
    }
}
