package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.pojo.dto.MovieDto;
import wsg.tools.boot.pojo.dto.SeasonDto;
import wsg.tools.boot.pojo.dto.SeriesDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.error.DataIntegrityException;
import wsg.tools.boot.pojo.error.SiteException;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.BiResult;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.boot.service.intf.VideoManager;
import wsg.tools.common.io.Rundll32;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.MarkEnum;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final ResourceService resourceService;
    private final VideoManager videoManager;

    @Autowired
    public SubjectController(SubjectService subjectService, ResourceService resourceService, VideoManager videoManager) {
        this.subjectService = subjectService;
        this.resourceService = resourceService;
        this.videoManager = videoManager;
    }

    /**
     * Import subjects from Douban of the given user.
     */
    @PostMapping(value = "/douban/import")
    @ResponseBody
    public BatchResult<Long> importDouban(long user, LocalDate since) {
        BatchResult<Long> result = BatchResult.empty();
        for (MarkEnum mark : MarkEnum.values()) {
            result = result.plus(subjectService.importDouban(user, since, mark));
        }
        return result;
    }

    @PostMapping(value = "/import")
    @ResponseBody
    public ResponseEntity<String> importSubject(Long id) {
        if (id == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID must be not null.");
        }
        try {
            subjectService.importSubjectByDb(id);
            return ResponseEntity.ok("Success");
        } catch (NotFoundException | SiteException | DataIntegrityException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @GetMapping(path = "/subject/index")
    public String subjects(Model model) {
        List<MovieDto> subjects = subjectService.listMovies().getRecords().stream().map(movie -> {
            MovieDto movieDto = new MovieDto();
            movieDto.setId(movie.getId());
            movieDto.setImdbId(movie.getImdbId());
            movieDto.setTitle(movie.getTitle());
            movieDto.setYear(movie.getYear().getValue());
            movieDto.setLanguages(movie.getLanguages());
            movieDto.setDbId(movie.getDbId());
            movieDto.setDurations(movie.getDurations().stream().map(Duration::toMinutes).map(String::valueOf)
                    .collect(Collectors.joining("/")));
            movieDto.setStatus(videoManager.getStatus(movie));
            movieDto.setGmtModified(movie.getGmtModified());
            return movieDto;
        }).sorted((o1, o2) -> {
            int dif = o2.getStatus().getCode() - o1.getStatus().getCode();
            if (dif != 0) {
                return dif;
            }
            return o2.getGmtModified().compareTo(o1.getGmtModified());
        }).collect(Collectors.toList());
        model.addAttribute("movies", subjects);
        Map<SeriesEntity, List<SeasonEntity>> map = subjectService.listSeries();
        List<SeriesDto> tvs = map.entrySet().stream().map(entry -> {
            SeriesDto seriesDto = new SeriesDto();
            seriesDto.setSeries(entry.getKey());
            List<SeasonDto> seasons = entry.getValue().stream().map(season -> {
                SeasonDto seasonDto = new SeasonDto();
                seasonDto.setId(season.getId());
                seasonDto.setTitle(season.getTitle());
                seasonDto.setYear(season.getYear().getValue());
                seasonDto.setDbId(season.getDbId());
                seasonDto.setDurations(season.getDurations().stream().map(Duration::toMinutes).map(String::valueOf)
                        .collect(Collectors.joining("/")));
                seasonDto.setCurrentSeason(season.getCurrentSeason());
                seasonDto.setEpisodesCount(season.getEpisodesCount());
                seasonDto.setStatus(videoManager.getStatus(season));
                return seasonDto;
            }).collect(Collectors.toList());
            seriesDto.setSeasons(seasons);
            seriesDto.setUnarchived((int) seasons.stream()
                    .filter(season -> season.getStatus() != VideoStatus.ARCHIVED || season.getStatus() != VideoStatus.COMING).count());
            return seriesDto;
        }).sorted(((o1, o2) -> {
            int dif = o2.getUnarchived() - o1.getUnarchived();
            if (dif != 0) {
                return dif;
            }
            return o2.getSeries().getYear().compareTo(o1.getSeries().getYear());
        })).collect(Collectors.toList());
        model.addAttribute("tvs", tvs);
        return "video/subject/index";
    }

    @PostMapping(path = "/movie/resources")
    public String movieResources(Long id, String key, Model model) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return "error/notFound";
        }
        MovieEntity movieEntity = optional.get();
        model.addAttribute("movie", movieEntity);
        if (StringUtils.isBlank(key)) {
            key = movieEntity.getTitle();
        }
        model.addAttribute("items", resourceService.search(key, movieEntity.getDbId(), movieEntity.getImdbId()));
        return "video/movie/resources";
    }

    @PostMapping(path = "/movie/archive")
    @ResponseBody
    public ResponseEntity<?> archiveMovie(Long id) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.ok(videoManager.archive(optional.get()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "/series/resources")
    public String seriesResources(Long id, String key, Model model) {
        BiResult<SeriesEntity, List<SeasonEntity>> result = subjectService.getSeries(id);
        if (result.getLeft() == null) {
            return "error/notFound";
        }
        SeriesEntity seriesEntity = result.getLeft();
        model.addAttribute("series", seriesEntity);
        List<SeasonEntity> seasons = result.getRight();
        model.addAttribute("seasons", seasons);
        if (StringUtils.isBlank(key)) {
            key = seriesEntity.getTitle();
        }
        Set<ResourceItemEntity> items = new HashSet<>(resourceService.search(key, null, seriesEntity.getImdbId()));
        seasons.forEach(seasonEntity -> items.addAll(resourceService.search(null, seasonEntity.getDbId(), null)));
        model.addAttribute("items", items);
        return "video/series/resources";
    }

    @PostMapping(path = "/season/archive")
    @ResponseBody
    public ResponseEntity<?> archiveSeason(Long id) {
        Optional<SeasonEntity> optional = subjectService.getSeason(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            return ResponseEntity.ok(videoManager.archive(optional.get()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping(path = "/open")
    @ResponseBody
    public ResponseEntity<?> open(long id) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Optional<File> file = videoManager.getFile(optional.get());
        if (file.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        try {
            Rundll32.openFile(file.get());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
