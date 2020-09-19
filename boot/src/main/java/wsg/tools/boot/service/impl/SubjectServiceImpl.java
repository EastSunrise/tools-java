package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.config.VideoConfig;
import wsg.tools.boot.dao.jpa.mapper.SubjectRepository;
import wsg.tools.boot.dao.jpa.mapper.UserRecordRepository;
import wsg.tools.boot.pojo.base.GenericResult;
import wsg.tools.boot.pojo.base.ListResult;
import wsg.tools.boot.pojo.entity.*;
import wsg.tools.boot.pojo.result.ImportResult;
import wsg.tools.boot.pojo.result.SiteResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.douban.object.DoubanMovie;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbTitle;
import wsg.tools.internet.video.entity.imdb.object.ImdbEpisode;
import wsg.tools.internet.video.entity.imdb.object.ImdbMovie;
import wsg.tools.internet.video.entity.imdb.object.ImdbSeries;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;
import wsg.tools.internet.video.entity.omdb.object.OmdbEpisode;
import wsg.tools.internet.video.entity.omdb.object.OmdbMovie;
import wsg.tools.internet.video.entity.omdb.object.OmdbSeries;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.MarkEnum;
import wsg.tools.internet.video.site.DoubanSite;

import java.io.IOException;
import java.time.Duration;
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
    private TransactionTemplate template;

    @Override
    @Transactional(rollbackFor = {IOException.class})
    public GenericResult<Long> insertSubjectByDb(long dbId) throws IOException {
        SiteResult<BaseDoubanSubject> subjectResult = config.doubanSubject(dbId);
        if (!subjectResult.isSuccess()) {
            Optional<IdRelation> optional = subjectRepository.findByDbId(dbId);
            return optional.map(idRelation -> GenericResult.of(idRelation.getId()))
                    .orElseGet(() -> new GenericResult<>(subjectResult.getMessage()));
        }

        BaseDoubanSubject subject = subjectResult.getData();
        String imdbId = subject.getImdbId();

        if (subject instanceof DoubanMovie) {
            return insertMovie(dbId, imdbId);
        }

        if (imdbId == null) {
            return new GenericResult<>("Can't save series %d without IMDb id.", dbId);
        }

        BaseImdbTitle imdbTitle = config.imdbTitle(imdbId);
        return insertSeries(imdbId, imdbTitle);
    }

    @Override
    @Transactional(rollbackFor = {IOException.class})
    public GenericResult<Long> insertSubjectByImdb(String imdbId, Long dbId) throws IOException {
        BaseImdbTitle imdbTitle = config.imdbTitle(imdbId);
        if (imdbTitle instanceof ImdbMovie) {
            if (dbId == null) {
                dbId = config.getDbIdByImdbId(imdbId);
            }
            return insertMovie(dbId, imdbId);
        }

        return insertSeries(imdbId, imdbTitle);
    }

    /**
     * At least one of the two ids is not null.
     */
    private GenericResult<Long> insertMovie(Long dbId, String imdbId) throws IOException {
        if (dbId != null) {
            Optional<IdRelation> optional = subjectRepository.findByDbId(dbId);
            if (optional.isPresent()) {
                return GenericResult.of(optional.get().getId());
            }
        }
        if (imdbId != null) {
            Optional<IdRelation> optional = subjectRepository.findByImdbId(imdbId);
            if (optional.isPresent()) {
                return GenericResult.of(optional.get().getId());
            }
        }
        MovieEntity entity = new MovieEntity();
        initEntity(entity, dbId, imdbId);
        if (dbId != null) {
            SiteResult<BaseDoubanSubject> subjectResult = config.doubanSubject(dbId);
            if (subjectResult.isSuccess()) {
                copyDouban(subjectResult.getData(), entity);
            }
        }
        if (StringUtils.isNotBlank(imdbId)) {
            copyImdb(imdbId, entity);
        }
        checkEntity(entity);
        return GenericResult.of(subjectRepository.insert(entity).getId());
    }

    /**
     * Id and title are both required not null.
     */
    private GenericResult<Long> insertSeries(String imdbId, BaseImdbTitle title) throws IOException {
        String seriesImdbId;
        if (title instanceof ImdbEpisode) {
            seriesImdbId = ((ImdbEpisode) title).getSeriesId();
            if (seriesImdbId == null) {
                return new GenericResult<>("Can't get series id for episode %s.", title.getText());
            }
        } else if (title instanceof ImdbSeries) {
            seriesImdbId = imdbId;
        } else {
            return new GenericResult<>("Unexpected type %s for title %s.", title.getClass().getSimpleName(), imdbId);
        }

        Optional<IdRelation> optional = subjectRepository.findByImdbId(seriesImdbId);
        if (optional.isPresent()) {
            return GenericResult.of(optional.get().getId());
        }
        SiteResult<List<String[]>> episodesResult = config.episodes(seriesImdbId);
        if (!episodesResult.isSuccess()) {
            return new GenericResult<>(episodesResult.getMessage());
        }

        SeriesEntity seriesEntity = new SeriesEntity();
        initEntity(seriesEntity, null, seriesImdbId);
        copyImdb(seriesImdbId, seriesEntity);
        checkEntity(seriesEntity);
        List<String[]> allEpisodes = episodesResult.getData();
        int seasonsCount = allEpisodes.size();
        if (seasonsCount == 0) {
            seasonsCount = 1;
        }
        seriesEntity.setSeasonsCount(seasonsCount);
        long seriesId = subjectRepository.insert(seriesEntity).getId();

        // insert seasons and episodes
        String[] season1Episodes = allEpisodes.isEmpty() ? new String[]{} : allEpisodes.get(0);
        insertSeason(seriesImdbId, season1Episodes, seriesId, 1);

        if (allEpisodes.size() > 1) {
            for (int i = 1; i < allEpisodes.size(); i++) {
                String[] episodes = allEpisodes.get(i);
                String seasonImdbId = episodes[1];
                if (seasonImdbId == null) {
                    log.error("None id of IMDb exists for season {} of series {}.", i + 1, seriesImdbId);
                    continue;
                }
                insertSeason(seasonImdbId, episodes, seriesId, i + 1);
            }
        }

        return GenericResult.of(seriesId);
    }

    /**
     * All args are required not null.
     */
    private void insertSeason(String seasonImdbId, String[] episodes, long seriesId, int currentSeason) throws IOException {
        Long seasonDbId = config.getDbIdByImdbId(seasonImdbId);
        if (seasonDbId == null) {
            log.error("Can't get douban id of the season by id of IMDb {}.", seasonImdbId);
            return;
        }

        SeasonEntity seasonEntity = new SeasonEntity();
        initEntity(seasonEntity, seasonDbId, null);
        SiteResult<BaseDoubanSubject> seasonResult = config.doubanSubject(seasonDbId);
        if (seasonResult.isSuccess()) {
            copyDouban(seasonResult.getData(), seasonEntity);
            seasonEntity.setEpisodesCount(((DoubanSeries) seasonResult.getData()).getEpisodesCount());
        }
        checkEntity(seasonEntity);
        if (seasonEntity.getEpisodesCount() == null || seasonEntity.getEpisodesCount() < 1) {
            seasonEntity.setEpisodesCount(episodes.length - 1);
        }
        seasonEntity.setSeriesId(seriesId);
        seasonEntity.setCurrentSeason(currentSeason);
        long seasonId = subjectRepository.insert(seasonEntity).getId();

        for (int i = 0; i < episodes.length; i++) {
            String episodeImdbId = episodes[i];
            if (StringUtils.isNotBlank(episodeImdbId)) {
                EpisodeEntity episodeEntity = new EpisodeEntity();
                initEntity(episodeEntity, null, episodeImdbId);
                copyImdb(episodeImdbId, episodeEntity);
                checkEntity(episodeEntity);
                episodeEntity.setCurrentEpisode(i);
                episodeEntity.setSeasonId(seasonId);
                subjectRepository.insert(episodeEntity);
            }
        }
    }

    private void checkEntity(SubjectEntity entity) {
        if (StringUtils.isBlank(entity.getText())) {
            entity.setText(null);
        }
        if (StringUtils.isBlank(entity.getTitle())) {
            entity.setTitle(null);
        }
        if (StringUtils.isBlank(entity.getOriginalTitle())) {
            entity.setOriginalTitle(null);
        }
        List<String> textAka = new HashSet<>(entity.getTextAka()).stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        entity.setTextAka(textAka.size() == 0 ? null : textAka);
        List<String> titleAka = new HashSet<>(entity.getTitleAka()).stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        entity.setTitleAka(titleAka.size() == 0 ? null : titleAka);
        List<LanguageEnum> languages = new LinkedHashSet<>(entity.getLanguages()).stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        entity.setLanguages(languages.size() == 0 ? null : languages);
        List<Duration> durations = new HashSet<>(entity.getDurations()).stream()
                .filter(Objects::nonNull).collect(Collectors.toList());
        entity.setDurations(durations.size() == 0 ? null : durations);
    }

    private void initEntity(SubjectEntity entity, Long dbId, String imdbId) {
        entity.setDbId(dbId);
        entity.setImdbId(imdbId);
        if (entity.getTextAka() == null) {
            entity.setTextAka(new ArrayList<>());
        }
        if (entity.getTitleAka() == null) {
            entity.setTitleAka(new ArrayList<>());
        }
        if (entity.getLanguages() == null) {
            entity.setLanguages(new ArrayList<>());
        }
        if (entity.getDurations() == null) {
            entity.setDurations(new ArrayList<>());
        }
    }

    private void copyDouban(BaseDoubanSubject subject, SubjectEntity entity) {
        entity.setYear(subject.getYear());
        entity.setOriginalTitle(subject.getOriginalTitle());
        entity.setTitle(subject.getTitle());
        entity.getLanguages().addAll(subject.getLanguages());
        entity.getDurations().add(subject.getDuration());
        if (subject.getExtDurations() != null) {
            entity.getDurations().addAll(subject.getExtDurations());
        }
    }

    private void copyImdb(String imdbId, SubjectEntity entity) throws IOException {
        SiteResult<BaseOmdbTitle> omdbResult = config.omdbTitle(imdbId);
        if (omdbResult.isSuccess()) {
            BaseOmdbTitle title = omdbResult.getData();
            entity.setText(title.getText());
            addAll(entity.getLanguages(), title.getLanguages());
            if (title.getRuntime() != null) {
                entity.getDurations().add(title.getRuntime().getDuration());
            }
            if (title instanceof OmdbSeries) {
                entity.setYear(((OmdbSeries) title).getYear().getStart());
            } else if (title instanceof OmdbMovie) {
                entity.setYear(((OmdbMovie) title).getYear());
            } else {
                entity.setYear(((OmdbEpisode) title).getYear());
            }
        } else {
            log.error(omdbResult.getMessage());
        }

        BaseImdbTitle imdbTitle = config.imdbTitle(imdbId);
        entity.setText(imdbTitle.getText());
    }

    private <T> void addAll(List<T> target, List<T> added) {
        if (added != null) {
            target.addAll(added);
        }
    }

    @Override
    public ImportResult importDouban(long userId, LocalDate since) {
        if (since == null) {
            since = userRecordRepository.findMaxMarkDate();
            if (since == null) {
                since = DoubanSite.DOUBAN_START_DATE;
            }
        }
        log.info("Start to import douban subjects of {} since {}", userId, since);
        int count = 0;
        Map<Long, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (MarkEnum mark : MarkEnum.values()) {
            Map<Long, LocalDate> subjects;
            try {
                subjects = config.collectUserSubjects(userId, since, mark);
            } catch (HttpResponseException e) {
                log.error(e.getMessage());
                continue;
            } catch (IOException e) {
                return new ImportResult(e);
            }
            for (Map.Entry<Long, LocalDate> entry : subjects.entrySet()) {
                GenericResult<Long> result = template.execute(status -> {
                    try {
                        return insertSubjectByDb(entry.getKey());
                    } catch (IOException e) {
                        return new GenericResult<>(e);
                    }
                });
                Objects.requireNonNull(result);
                if (result.isSuccess()) {
                    count++;
                    UserRecordEntity entity = new UserRecordEntity();
                    entity.setMark(mark);
                    entity.setMarkDate(entry.getValue());
                    entity.setSubjectId(result.getData());
                    entity.setUserId(userId);
                    userRecordRepository.updateOrInsert(entity,
                            () -> userRecordRepository.findBySubjectIdAndUserId(result.getData(), userId));
                } else {
                    fails.put(entry.getKey(), result.getMessage());
                }
            }
        }
        return new ImportResult(count, fails);
    }

    @Override
    public ImportResult importManually(List<DefaultKeyValue<String, Long>> ids) {
        log.info("Start to import subjects manually.");
        int count = 0;
        Map<String, String> fails = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        for (DefaultKeyValue<String, Long> entry : ids) {
            GenericResult<Long> result = template.execute(status -> {
                try {
                    return insertSubjectByImdb(entry.getKey(), entry.getValue());
                } catch (IOException e) {
                    return new GenericResult<>(e);
                }
            });
            Objects.requireNonNull(result);
            if (result.isSuccess()) {
                count++;
            } else {
                fails.put(entry.getKey(), result.getMessage());
            }
        }
        return new ImportResult(count, fails);
    }

    @Override
    public ListResult<SubjectEntity> export() {
        return new ListResult<>(subjectRepository.findAll().stream()
                .filter(entity -> entity instanceof MovieEntity || entity instanceof SeriesEntity)
                .filter(entity -> entity.getDbId() == null || entity.getImdbId() == null
                        || entity.getTitle() == null || entity.getText() == null
                        || entity.getYear() == null || entity.getLanguages() == null)
                .collect(Collectors.toList())
        );
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
    public void setTemplate(TransactionTemplate template) {
        this.template = template;
    }

    @Autowired
    public void setConfig(VideoConfig config) {
        this.config = config;
    }
}
