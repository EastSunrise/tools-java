package wsg.tools.boot.dao.api;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.enums.TypeEnum;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.video.entity.douban.DoubanSubject;
import wsg.tools.internet.video.entity.imdb.ImdbSubject;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.OmdbSite;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Configuration for video to get instances of video sites.
 *
 * @author Kingen
 * @since 2020/6/26
 */
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfig implements InitializingBean {

    @Value("${douban.api.key}")
    private String doubanApiKey;
    private DoubanSite doubanSite;

    @Value("${omdb.api.key}")
    private String omdbApiKey;
    private OmdbSite omdbSite;

    public SubjectEntity getImdb(String imdbId) throws HttpResponseException {
        Objects.requireNonNull(imdbId);
        ImdbSubject imdbSubject = omdbSite.getSubjectById(imdbId);
        SubjectEntity subject = BeanUtilExt.convert(imdbSubject, SubjectEntity.class);
        subject.setType(TypeEnum.of(imdbSubject.getType()));
        if (subject.getDurations() == null) {
            subject.setDurations(new ArrayList<>());
        }
        subject.getDurations().add(imdbSubject.getRuntime());
        return subject;
    }

    public SubjectEntity getDouban(long dbId) throws HttpResponseException {
        DoubanSubject doubanSubject = doubanSite.movieSubject(dbId);
        SubjectEntity subject = BeanUtilExt.convert(doubanSubject, SubjectEntity.class);
        subject.setType(TypeEnum.of(doubanSubject.getSubtype()));
        if (CollectionUtils.isNotEmpty(doubanSubject.getAka())) {
            List<String> textAka = new ArrayList<>(), titleAka = new ArrayList<>();
            for (String aka : doubanSubject.getAka()) {
                if (StringUtilsExt.hasChinese(aka)) {
                    titleAka.add(aka);
                } else {
                    textAka.add(aka);
                }
            }
            if (CollectionUtils.isNotEmpty(textAka)) {
                subject.setTextAka(textAka);
            }
            if (CollectionUtils.isNotEmpty(titleAka)) {
                subject.setTitleAka(titleAka);
            }
        }
        return subject;
    }

    public List<SubjectEntity> collectUserMovies(long userId, LocalDate since) throws HttpResponseException {
        return doubanSite.collectUserMovies(userId, since).stream().map(
                s -> BeanUtilExt.convert(s, SubjectEntity.class)).collect(Collectors.toList());
    }

    @Override
    public void afterPropertiesSet() {
        doubanSite = new DoubanSite(doubanApiKey);
        omdbSite = new OmdbSite(omdbApiKey);
    }
}
