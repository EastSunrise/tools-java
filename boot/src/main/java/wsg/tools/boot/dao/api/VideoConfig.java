package wsg.tools.boot.dao.api;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.video.entity.douban.pojo.Subject;
import wsg.tools.internet.video.entity.imdb.ImdbSubject;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.OmdbSite;

import javax.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
        SubjectEntity subject = null;
        if (dbId != null) {
            try {
                subject = getDoubanSubject(dbId);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        if (subject != null && subject.getImdbId() != null) {
            imdbId = subject.getImdbId();
        }
        if (imdbId != null) {
            try {
                SubjectEntity entity = getImdbSubject(imdbId);
                if (subject != null) {
                    BeanUtilExt.copyPropertiesExceptNull(subject, entity);
                } else {
                    subject = entity;
                }
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        return subject;
    }

    public SubjectEntity getImdbSubject(String imdbId) throws HttpResponseException {
        Objects.requireNonNull(imdbId);
        ImdbSubject imdbSubject = omdbSite.getSubjectById(imdbId);
        SubjectEntity subject = BeanUtilExt.convert(imdbSubject, SubjectEntity.class);
        subject.setType(VideoTypeEnum.of(imdbSubject.getType()));
        if (subject.getDurations() == null) {
            subject.setDurations(new ArrayList<>());
        }
        subject.getDurations().add(imdbSubject.getRuntime());
        return subject;
    }

    public SubjectEntity getDoubanSubject(long dbId) throws HttpResponseException {
        Pair<Subject, String> pair = doubanSite.movieSubject(dbId);
        Subject subject = pair.getKey();
        SubjectEntity entity = BeanUtilExt.convert(subject, SubjectEntity.class);
        entity.setId(null);
        entity.setDbId(subject.getId());
        entity.setType(VideoTypeEnum.of(subject.getSubtype()));
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
        entity.setImdbId(pair.getValue());
        return entity;
    }

    public List<SubjectEntity> collectUserSubjects(long userId, LocalDate since) throws HttpResponseException {
        List<SubjectEntity> entities = new ArrayList<>();
        for (MarkEnum mark : MarkEnum.values()) {
            Map<Long, LocalDate> map = doubanSite.collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark);
            for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
                try {
                    SubjectEntity entity = getDoubanSubject(entry.getKey());
                    entity.setMark(mark);
                    entity.setMarkDate(entry.getValue());
                    entities.add(entity);
                } catch (HttpResponseException e) {
                    log.error(e.getMessage());
                }
            }
        }
        return entities;
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
