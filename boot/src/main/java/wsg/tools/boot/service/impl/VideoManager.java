package wsg.tools.boot.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.boot.pojo.entity.SeriesEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.common.constant.SignConstants;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Service
@PropertySource("classpath:config/private/video.properties")
public class VideoManager extends BaseServiceImpl {

    private static final String NAME_SEPARATOR = SignConstants.UNDERLINE;
    private static final String TV_DIR = "TV";
    private static final String MOVIE_DIR = "Movies";

    private SubjectRepository subjectRepository;

    /**
     * Obtains relative path of the corresponding video of the given entity.
     */
    public String getLocation(SubjectEntity entity) {
        Objects.requireNonNull(entity, "Given entity mustn't be null.");
        Objects.requireNonNull(entity.getLanguages(), "Languages of a subject mustn't be null.");
        Objects.requireNonNull(entity.getYear(), "Year of a subject mustn't be null.");
        Objects.requireNonNull(entity.getTitle(), "Title of a subject mustn't be null.");

        StringBuilder builder = new StringBuilder()
                .append(entity.getLanguages().get(0).getTitle()).append(File.separator).append(entity.getYear());
        if (StringUtils.isNotBlank(entity.getOriginalTitle())) {
            builder.append(NAME_SEPARATOR).append(entity.getOriginalTitle());
        }
        String filepath = builder.append(NAME_SEPARATOR).append(entity.getTitle()).toString();

        if (entity instanceof MovieEntity) {
            return StringUtils.joinWith(File.separator, MOVIE_DIR, filepath);
        }

        if (entity instanceof SeriesEntity) {
            return StringUtils.joinWith(File.separator, TV_DIR, filepath);
        }

        if (entity instanceof SeasonEntity) {
            SeasonEntity seasonEntity = (SeasonEntity) entity;
            Objects.requireNonNull(seasonEntity.getSeriesId(), "Series id of a season subject mustn't be null.");
            Optional<SubjectEntity> optional = subjectRepository.findById(seasonEntity.getSeriesId());
            if (optional.isEmpty()) {
                throw new EntityNotFoundException("Can't find entity of " + seasonEntity.getSeriesId());
            }
            return StringUtils.joinWith(File.separator,
                    getLocation(optional.get()), String.format("S%2d", seasonEntity.getCurrentSeason()));
        }

        throw new IllegalArgumentException("Unknown type of subject " + entity.getId());
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }
}
