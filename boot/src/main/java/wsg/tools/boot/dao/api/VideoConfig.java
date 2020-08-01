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
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.enums.VideoTypeEnum;
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

        SubjectEntity entity = new SubjectEntity();
        if (dbId != null) {
            try {
                Subject subject = doubanSite.apiMovieSubject(dbId);
                BeanUtilExt.copyPropertiesExceptNull(entity, subject, true, true);
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
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
        if (imdbId != null) {
            try {
                ImdbSubject subject = omdbSite.getSubjectById(imdbId);
                BeanUtilExt.copyPropertiesExceptNull(entity, subject, true, true);
                entity.setType(VideoTypeEnum.of(subject.getType()));
                if (entity.getDurations() == null) {
                    entity.setDurations(new ArrayList<>());
                }
                entity.getDurations().add(subject.getRuntime());
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
            }
        }
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
