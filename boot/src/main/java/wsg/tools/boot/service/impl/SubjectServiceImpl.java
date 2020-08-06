package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import wsg.tools.boot.common.BeanUtilExt;
import wsg.tools.boot.dao.api.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.NotFoundRepository;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.dao.jpa.mapper.UserRecordRepository;
import wsg.tools.boot.pojo.base.PageResult;
import wsg.tools.boot.pojo.base.Result;
import wsg.tools.boot.pojo.dto.QuerySubjectDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.NotFoundEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity;
import wsg.tools.boot.pojo.entity.SubjectEntity_;
import wsg.tools.boot.pojo.entity.UserRecordEntity;
import wsg.tools.boot.pojo.result.ImportResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.internet.video.entity.douban.container.BoxResult;
import wsg.tools.internet.video.entity.douban.container.RankedResult;
import wsg.tools.internet.video.entity.douban.pojo.SimpleSubject;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.CityEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;
import wsg.tools.internet.video.site.ImdbSite;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implement of subject service.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Service
public class SubjectServiceImpl extends BaseServiceImpl implements SubjectService {

    private VideoConfig config;
    private SubjectRepository subjectRepository;
    private UserRecordRepository userRecordRepository;
    private NotFoundRepository notFoundRepository;

    @Override
    public ImportResult importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.DOUBAN_START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        List<Long> notFounds = new ArrayList<>();
        int added = 0, exists = 0;
        for (MarkEnum mark : MarkEnum.values()) {
            Map<Long, LocalDate> map;
            try {
                map = config.doubanSite().collectUserSubjects(userId, since, CatalogEnum.MOVIE, mark);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
                continue;
            }
            for (Map.Entry<Long, LocalDate> entry : map.entrySet()) {
                Long id = null;
                Optional<SubjectEntity> optional = subjectRepository.findByDbId(entry.getKey());
                if (optional.isEmpty()) {
                    SubjectEntity entity = config.getSubjectEntity(entry.getKey(), null);
                    if (entity != null) {
                        id = subjectRepository.insert(entity).getId();
                        added++;
                    } else {
                        notFounds.add(entry.getKey());
                    }
                } else {
                    id = optional.get().getId();
                    exists++;
                }
                if (id != null) {
                    UserRecordEntity entity = new UserRecordEntity();
                    entity.setMark(mark);
                    entity.setMarkDate(entry.getValue());
                    entity.setSubjectId(id);
                    entity.setUserId(userId);
                    Long finalId = id;
                    userRecordRepository.updateOrInsert(entity, () -> userRecordRepository.findBySubjectIdAndUserId(finalId, userId));
                }
            }
        }
        insertNotFound(notFounds);
        return new ImportResult(added, exists, notFounds);
    }

    @Override
    public ImportResult importImdbIds(List<String> ids) {
        log.info("Start to import imdb watchlist.");
        int added = 0, exists = 0;
        List<String> notFounds = new ArrayList<>();
        for (String id : ids) {
            if (subjectRepository.findByImdbId(id).isEmpty()) {
                SubjectEntity entity = config.getSubjectEntity(null, id);
                if (entity != null) {
                    subjectRepository.insert(entity);
                    added++;
                } else {
                    notFounds.add(id);
                }
            } else {
                exists++;
            }
        }
        insertNotFound(notFounds);
        return new ImportResult(added, exists, notFounds);
    }

    @Override
    public ImportResult importManually(List<MutablePair<Long, String>> pairs) {
        log.info("Start to import subjects manually.");
        int added = 0, exists = 0;
        List<Object> notFounds = new ArrayList<>(), all = new ArrayList<>();
        for (MutablePair<Long, String> pair : pairs) {
            Optional<SubjectEntity> optional = Optional.empty();
            if (pair.left != null) {
                all.add(pair.left);
                optional = subjectRepository.findByDbId(pair.left);
            }
            if (pair.right != null) {
                all.add(pair.right);
                if (optional.isEmpty()) {
                    optional = subjectRepository.findByImdbId(pair.right);
                }
            }
            if (optional.isEmpty()) {
                SubjectEntity entity = config.getSubjectEntity(pair.left, pair.right);
                if (entity != null) {
                    subjectRepository.insert(entity);
                    added++;
                } else {
                    if (pair.left != null) {
                        notFounds.add(pair.left);
                    }
                    if (pair.right != null) {
                        notFounds.add(pair.right);
                    }
                }
            } else {
                exists++;
            }
        }
        notFoundRepository.deleteInBatch(all.stream().map(o -> {
            NotFoundEntity entity = new NotFoundEntity();
            entity.setId(String.valueOf(o));
            return entity;
        }).collect(Collectors.toList()));
        insertNotFound(notFounds);
        return new ImportResult(added, exists, notFounds);
    }

    @Override
    public ImportResult top250() {
        log.info("Start to update top 250.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieTop250().getRight());
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    @Override
    public ImportResult movieWeekly() {
        log.info("Start to update weekly movies.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieWeekly().getSubjects().stream()
                    .map(RankedResult.RankedSubject::getSubject).collect(Collectors.toList()));
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    @Override
    public ImportResult movieUsBox() {
        log.info("Start to update us box movies.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieUsBox().getSubjects().stream()
                    .map(BoxResult.BoxSubject::getSubject).collect(Collectors.toList()));
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    @Override
    public ImportResult movieInTheatre() {
        log.info("Start to update movies in theaters.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieInTheaters(CityEnum.BEIJING).getRight());
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    @Override
    public ImportResult movieComingSoon() {
        log.info("Start to update movies coming soon.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieComingSoon().getRight());
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    @Override
    public ImportResult newMovies() {
        log.info("Start to update new movies.");
        try {
            return batchInsertDoubanIgnore(config.doubanSite().apiMovieNewMovies().getRight());
        } catch (HttpResponseException e) {
            return new ImportResult(e);
        }
    }

    private ImportResult batchInsertDoubanIgnore(List<SimpleSubject> subjects) {
        int added = 0, exists = 0;
        List<Long> notFounds = new ArrayList<>();
        for (SimpleSubject subject : subjects) {
            if (subjectRepository.findByDbId(subject.getId()).isEmpty()) {
                SubjectEntity entity = config.getSubjectEntity(subject.getId(), null);
                if (entity != null) {
                    subjectRepository.insert(entity);
                    added++;
                } else {
                    notFounds.add(subject.getId());
                }
            } else {
                exists++;
            }
        }
        insertNotFound(notFounds);
        return new ImportResult(added, exists, notFounds);
    }

    private void insertNotFound(List<?> notFounds) {
        List<NotFoundEntity> all = notFoundRepository.findAll();
        Set<String> set = all.stream().map(NotFoundEntity::getId).collect(Collectors.toSet());
        notFounds.forEach(id -> {
            if (!set.contains(String.valueOf(id))) {
                NotFoundEntity entity = new NotFoundEntity();
                entity.setId(String.valueOf(id));
                notFoundRepository.insert(entity);
            }
        });
    }

    @Override
    public Result batchUpdate(List<SubjectDto> subjects) {
        log.info("Start to update list of subjects.");
        int count = 0;
        for (SubjectDto subject : subjects) {
            SubjectEntity entity = config.getSubjectEntity(subject.getDbId(), subject.getImdbId());
            if (subject.getId() != null && entity != null) {
                entity.setId(subject.getId());
                if (subjectRepository.updateById(entity) != null) {
                    count++;
                }
            }
        }
        Result result = Result.success();
        result.put("total", subjects.size());
        result.put("updated", count);
        result.put("others", subjects.size() - count);
        return result;
    }

    @Override
    public PageResult<SubjectDto> list(QuerySubjectDto querySubjectDto, Pageable pageable) {
        Specification<SubjectEntity> spec = (Specification<SubjectEntity>) (root, query, builder) -> {
            Predicate predicate = getPredicate(querySubjectDto, root, builder, SubjectEntity.class);
            if (querySubjectDto.getIncomplete()) {
                Predicate or = builder.or(root.get(SubjectEntity_.imdbId).isNull(), root.get(SubjectEntity_.dbId).isNull());
                return builder.and(predicate, or);
            }
            return predicate;
        };
        if (pageable == null) {
            return PageResult.of(subjectRepository.findAll(spec).stream()
                    .map(entity -> BeanUtilExt.convert(entity, SubjectDto.class)).collect(Collectors.toList()));
        }
        return PageResult.of(subjectRepository.findAll(spec, pageable).map(entity -> BeanUtilExt.convert(entity, SubjectDto.class)));
    }

    @Override
    public List<Object> notFounds() {
        return notFoundRepository.findAll().stream().map(entity -> {
            String id = entity.getId();
            return id.startsWith(ImdbSite.IMDB_TITLE_PREFIX) ? id : Long.valueOf(id);
        }).collect(Collectors.toList());
    }

    @Autowired
    public void setNotFoundRepository(NotFoundRepository notFoundRepository) {
        this.notFoundRepository = notFoundRepository;
    }

    @Autowired
    public void setUserRecordRepository(UserRecordRepository userRecordRepository) {
        this.userRecordRepository = userRecordRepository;
    }

    @Autowired
    public void setSubjectRepository(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @Autowired
    public void setConfig(VideoConfig config) {
        this.config = config;
    }
}
