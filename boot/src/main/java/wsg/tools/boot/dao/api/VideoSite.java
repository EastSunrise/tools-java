package wsg.tools.boot.dao.api;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.StatusEnum;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.internet.video.entity.SimpleSubject;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;
import wsg.tools.internet.video.site.OmdbSite;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuration for video, transfer objects of other modules to those of this module.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoSite implements InitializingBean {

    private ImdbSite imdbSite;

    @Value("${douban.api.key}")
    private String doubanApiKey;
    private DoubanSite doubanSite;

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    private OmdbSite omdbSite;

    /**
     * Obtains info of a subject from {@link DoubanSite}.
     */
    public SubjectDto subjectDto(long id) throws IOException, URISyntaxException {
        SubjectDto subjectDto = new SubjectDto();
        BeanUtils.copyProperties(doubanSite.movieSubject(id), subjectDto);
        Subject subject = doubanSite.apiMovieSubject(id);
        BeanUtils.copyProperties(subject, subjectDto, "imdbId");
        subjectDto.setSubtype(SubtypeEnum.of(subject.getSubtype()));
        return subjectDto;
    }

    /**
     * Obtains info of a subject from {@link OmdbSite} and {@link ImdbSite}.
     */
    public SubjectDto subjectDto(String imdbId) throws IOException, URISyntaxException {
        SubjectDto subjectDto = new SubjectDto();
        Subject subject = omdbSite.getSubjectById(imdbId);
        BeanUtils.copyProperties(subject, subjectDto);
        subjectDto.setSubtype(SubtypeEnum.of(subject.getSubtype()));
        if (subject.getRuntime() != null) {
            if (subjectDto.getDurations() == null) {
                subjectDto.setDurations(new HashSet<>());
            }
            subjectDto.getDurations().add(subject.getRuntime());
        }
        return subjectDto;
    }

    public List<SubjectDto> userSubjects(long userId, LocalDate startDate) throws IOException, URISyntaxException {
        List<SimpleSubject> subjects = doubanSite.collectUserMovies(userId, startDate);
        return subjects.stream().map(s -> {
            SubjectDto subjectDto = new SubjectDto();
            BeanUtils.copyProperties(s, subjectDto);
            subjectDto.setStatus(StatusEnum.of(s.getRecord()));
            return subjectDto;
        }).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        doubanSite = new DoubanSite(doubanApiKey);
        omdbSite = new OmdbSite(omdbApiKey);
        imdbSite = new ImdbSite();
    }
}
