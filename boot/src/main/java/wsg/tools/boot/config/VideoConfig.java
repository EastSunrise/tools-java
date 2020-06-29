package wsg.tools.boot.config;

import lombok.Getter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.entity.subject.dto.SubjectDto;
import wsg.tools.boot.entity.subject.enums.SubtypeEnum;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;
import wsg.tools.internet.video.site.OmdbSite;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;

/**
 * Configuration for video.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Getter
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfig {

    private static DoubanSite doubanSite;
    private static ImdbSite imdbSite;
    private static OmdbSite omdbSite;

    @Value("${douban.api.key}")
    private String doubanApiKey;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    /**
     * Obtains an instance of {@link DoubanSite}
     */
    public DoubanSite doubanSite() {
        if (doubanSite == null) {
            doubanSite = new DoubanSite(doubanApiKey);
        }
        return doubanSite;
    }

    /**
     * Obtains an instance of {@link OmdbSite}
     */
    public OmdbSite omdbSite() {
        if (omdbSite == null) {
            omdbSite = new OmdbSite(omdbApiKey);
        }
        return omdbSite;
    }

    /**
     * Obtains an instance of {@link ImdbSite}
     */
    public ImdbSite imdbSite() {
        if (imdbSite == null) {
            imdbSite = new ImdbSite();
        }
        return imdbSite;
    }

    /**
     * Obtains full info of a subject.
     */
    public SubjectDto subjectDto(long id) throws IOException, URISyntaxException {
        SubjectDto subjectDto = new SubjectDto();
        DoubanSite doubanSite = doubanSite();
        BeanUtils.copyProperties(doubanSite.movieSubject(id), subjectDto);
        Subject subject = doubanSite.apiMovieSubject(id);
        BeanUtils.copyProperties(subject, subjectDto, "imdbId");
        subjectDto.setSubtype(SubtypeEnum.of(subject.getSubtype()));
        return subjectDto;
    }

    public SubjectDto subjectDto(String imdbId) throws IOException, URISyntaxException {
        SubjectDto subjectDto = new SubjectDto();
        OmdbSite omdbSite = omdbSite();
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
}
