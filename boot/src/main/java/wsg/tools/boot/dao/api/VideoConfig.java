package wsg.tools.boot.dao.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.boot.pojo.entity.SeriesEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.video.entity.douban.pojo.Subject;
import wsg.tools.internet.video.entity.douban.pojo.SubjectInfo;
import wsg.tools.internet.video.entity.imdb.ImdbSubject;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.OmdbSite;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for video to get instances of video sites.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Slf4j
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfig implements InitializingBean {

    @Value("${douban.api.key}")
    private String doubanApiKey;
    private DoubanSite doubanSite;

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    private OmdbSite omdbSite;

    @Nullable
    public SubjectEntity getSubjectEntity(Long dbId, String imdbId) {
        if (dbId == null && StringUtils.isBlank(imdbId)) {
            throw new IllegalArgumentException("Can't get entity without id.");
        }
        if (dbId == null) {
            try {
                SubjectInfo info = doubanSite.apiMovieImdb(imdbId);
                dbId = DoubanSite.parseAlt(info.getAlt());
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        } else if (StringUtils.isBlank(imdbId)) {
            try {
                imdbId = doubanSite.getImdbId(dbId);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }

        SubjectEntity doubanSubject = getDoubanSubject(dbId);
        SubjectEntity imdbSubject = getImdbSubject(imdbId);
        if (doubanSubject == null) {
            return imdbSubject;
        }
        if (imdbSubject == null) {
            return doubanSubject;
        }
        BeanUtilExt.copyPropertiesExceptNull(doubanSubject, imdbSubject, false, true);
        return doubanSubject;
    }

    private SubjectEntity getDoubanSubject(Long dbId) {
        if (dbId == null) {
            return null;
        }
        Subject subject;
        try {
            subject = doubanSite.apiMovieSubject(dbId);
        } catch (HttpResponseException e) {
            return null;
        }
        SubjectEntity entity;
        switch (subject.getSubtype()) {
            case MOVIE:
                entity = BeanUtilExt.convert(subject, MovieEntity.class);
                break;
            case TV:
                entity = BeanUtilExt.convert(subject, SeasonEntity.class);
                break;
            default:
                throw new IllegalArgumentException("Subtype is null.");
        }
        entity.setId(null);
        entity.setDbId(subject.getId());
        if (CollectionUtils.isNotEmpty(subject.getAka())) {
            List<String> textAka = new ArrayList<>(), titleAka = new ArrayList<>();
            for (String aka : subject.getAka()) {
                if (StringUtilsExt.hasChinese(aka)) {
                    titleAka.add(aka);
                } else {
                    textAka.add(aka);
                }
            }
            if (CollectionUtils.isNotEmpty(textAka)) {
                entity.setTextAka(textAka);
            }
            if (CollectionUtils.isNotEmpty(titleAka)) {
                entity.setTitleAka(titleAka);
            }
        }
        return entity;
    }

    private SubjectEntity getImdbSubject(String imdbId) {
        if (StringUtils.isBlank(imdbId)) {
            return null;
        }
        ImdbSubject subject;
        try {
            subject = omdbSite.getSubjectById(imdbId);
        } catch (HttpResponseException e) {
            return null;
        }
        SubjectEntity entity;
        switch (subject.getType()) {
            case MOVIE:
                entity = BeanUtilExt.convert(subject, MovieEntity.class);
                break;
            case SERIES:
                entity = BeanUtilExt.convert(subject, SeriesEntity.class);
                break;
            case EPISODE:
                entity = BeanUtilExt.convert(subject, SeasonEntity.class);
                break;
            default:
                throw new IllegalArgumentException("IMDb type is null.");
        }
        if (entity.getDurations() == null) {
            entity.setDurations(new ArrayList<>());
        }
        entity.getDurations().add(subject.getRuntime());
        return entity;
    }

    @Override
    public void afterPropertiesSet() {
        doubanSite = new DoubanSite(doubanApiKey);
        omdbSite = new OmdbSite(omdbApiKey);
    }

    public DoubanSite doubanSite() {
        return doubanSite;
    }
}
