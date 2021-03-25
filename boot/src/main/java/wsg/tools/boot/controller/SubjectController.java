package wsg.tools.boot.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Functions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.DatabaseConfig;
import wsg.tools.boot.pojo.dto.MovieDto;
import wsg.tools.boot.pojo.dto.SeasonDto;
import wsg.tools.boot.pojo.dto.SeriesDto;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.io.Rundll32;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.OtherHttpResponseException;
import wsg.tools.internet.movie.common.enums.DoubanMark;

/**
 * API of video subjects.
 *
 * @author Kingen
 * @since 2020/6/22
 */
@Slf4j
@Controller
@RequestMapping("/video")
public class SubjectController extends AbstractController {

    private final SubjectService subjectService;
    private final VideoManager videoManager;

    @Autowired
    public SubjectController(SubjectService subjectService, VideoManager videoManager) {
        this.subjectService = subjectService;
        this.videoManager = videoManager;
    }

    private static <T extends IdentityEntity> ResponseEntity<VideoStatus> archive(
        Optional<T> optional,
        Functions.FailableFunction<T, VideoStatus, IOException> archive) {
        if (optional.isEmpty()) {
            return NOT_FOUND.build();
        }
        try {
            return OK.body(archive.apply(optional.get()));
        } catch (IOException e) {
            return SERVER_ERROR.build();
        }
    }

    private static <T extends IdentityEntity> ResponseEntity<Void> open(Optional<T> optional,
        Function<T, Optional<File>> getFile) {
        if (optional.isEmpty()) {
            return NOT_FOUND.build();
        }
        Optional<File> file = getFile.apply(optional.get());
        if (file.isEmpty()) {
            return NOT_FOUND.build();
        }
        try {
            Rundll32.openFile(file.get());
        } catch (IOException e) {
            log.error(e.getMessage());
            return SERVER_ERROR.build();
        }
        return OK.build();
    }

    /**
     * Import subjects from Douban of the given user.
     */
    @PostMapping("/import/douban")
    @ResponseBody
    public ResponseEntity<BatchResult<Long>> importDouban(Long user, LocalDate since) {
        if (user == null) {
            return BAD_REQUEST.build();
        }
        BatchResult<Long> result = BatchResult.empty();
        for (DoubanMark mark : DoubanMark.values()) {
            try {
                result = result.plus(subjectService.importDouban(user, since, mark));
            } catch (OtherHttpResponseException | LoginException e) {
                log.error(e.getMessage());
                return SERVER_ERROR.build();
            } catch (NotFoundException e) {
                log.error(e.getMessage());
                return NOT_FOUND.build();
            }
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import")
    @ResponseBody
    public ResponseEntity<String> importSubject(Long id) {
        if (id == null) {
            return ResponseEntity.badRequest().body("ID must be not null");
        }
        try {
            subjectService.importSubjectByDb(id);
        } catch (OtherHttpResponseException | DataIntegrityException | LoginException e) {
            return SERVER_ERROR.body(e.getMessage());
        } catch (NotFoundException e) {
            return NOT_FOUND.body(e.getMessage());
        }
        return OK.body(HttpStatus.OK.getReasonPhrase());
    }

    @GetMapping(path = "/movie/index")
    public String movies(Model model) {
        List<MovieDto> movies = new ArrayList<>();
        for (MovieEntity entity : subjectService.listMovies()) {
            MovieDto movie = MovieDto.fromEntity(entity);
            movie.setStatus(videoManager.getStatus(entity));
            movies.add(movie);
        }
        movies.sort((o1, o2) -> {
            int dif = o2.getStatus().getCode().compareTo(o1.getStatus().getCode());
            if (dif != 0) {
                return dif;
            }
            return o2.getGmtModified().compareTo(o1.getGmtModified());
        });
        model.addAttribute("movies", movies);
        return "video/movie/index";
    }

    @GetMapping(path = "/series/index")
    public String series(Model model) {
        List<SeriesDto> tvs = new ArrayList<>();
        for (Map.Entry<SeriesEntity, List<SeasonEntity>> entry : subjectService.listSeries()
            .entrySet()) {
            SeriesDto seriesDto = new SeriesDto();
            seriesDto.setSeries(entry.getKey());
            List<SeasonDto> seasons = new ArrayList<>();
            for (SeasonEntity entity : entry.getValue()) {
                SeasonDto seasonDto = SeasonDto.fromEntity(entity);
                seasonDto.setStatus(videoManager.getStatus(entity));
                seasons.add(seasonDto);
            }
            seriesDto.setSeasons(seasons);
            seriesDto.setUnarchived((int) seasons.stream()
                .filter(
                    season -> season.getStatus() != VideoStatus.ARCHIVED
                        || season.getStatus() != VideoStatus.COMING)
                .count());
            tvs.add(seriesDto);
        }
        tvs.sort(((o1, o2) -> {
            int dif = Integer.compare(o2.getUnarchived(), o1.getUnarchived());
            if (dif != 0) {
                return dif;
            }
            return o2.getSeries().getYear().compareTo(o1.getSeries().getYear());
        }));
        model.addAttribute("tvs", tvs);
        return "video/series/index";
    }

    @PostMapping(path = "/subject/archive")
    @ResponseBody
    public ResponseEntity<VideoStatus> archive(long id) {
        if (DatabaseConfig.isMovie(id)) {
            return archive(subjectService.getMovie(id), videoManager::archive);
        }
        if (DatabaseConfig.isSeason(id)) {
            return archive(subjectService.getSeason(id), videoManager::archive);
        }
        return BAD_REQUEST.build();
    }

    @PostMapping(path = "/open")
    @ResponseBody
    public ResponseEntity<Void> open(long id) {
        if (DatabaseConfig.isMovie(id)) {
            return open(subjectService.getMovie(id), videoManager::getFile);
        }
        if (DatabaseConfig.isSeason(id)) {
            return open(subjectService.getSeason(id), videoManager::getFile);
        }
        return BAD_REQUEST.build();
    }
}
