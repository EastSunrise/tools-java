package wsg.tools.boot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import wsg.tools.boot.common.enums.VideoStatus;
import wsg.tools.boot.config.VideoConfiguration;
import wsg.tools.boot.pojo.dto.SeasonDto;
import wsg.tools.boot.pojo.dto.SeriesDto;
import wsg.tools.boot.pojo.dto.SubjectDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.subject.MovieEntity;
import wsg.tools.boot.pojo.entity.subject.SeasonEntity;
import wsg.tools.boot.pojo.entity.subject.SeriesEntity;
import wsg.tools.boot.pojo.result.BatchResult;
import wsg.tools.boot.pojo.result.BiResult;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.boot.service.intf.SubjectService;
import wsg.tools.boot.service.intf.VideoManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    private final VideoConfiguration config;

    @Autowired
    public SubjectController(SubjectService subjectService, ResourceService resourceService, VideoManager videoManager, VideoConfiguration config) {
        this.subjectService = subjectService;
        this.resourceService = resourceService;
        this.videoManager = videoManager;
        this.config = config;
    }

    /**
     * Import subjects from Douban of the given user.
     */
    @PostMapping(value = "/douban/import")
    @ResponseBody
    public BatchResult<Long> importDouban(long user, LocalDate since) {
        return subjectService.importDouban(user, since);
    }

    @GetMapping(path = "/movie/index")
    public String movies(Model model) {
        List<MovieEntity> movies = subjectService.listMovies().getRecords();
        List<SubjectDto> subjects = movies.stream().map(movie -> {
            SubjectDto subjectDto = new SubjectDto();
            subjectDto.setId(movie.getId());
            subjectDto.setTitle(movie.getTitle());
            subjectDto.setDbId(movie.getDbId());
            subjectDto.setImdbId(movie.getImdbId());
            subjectDto.setArchived(videoManager.getFile(config.cdn(), movie).isPresent());
            return subjectDto;
        }).collect(Collectors.toList());
        model.addAttribute("movies", subjects);
        return "video/movie/index";
    }

    @GetMapping(path = "/movie/{id}/resources")
    public String movieResources(@PathVariable Long id, Model model) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return "error/notFound";
        }
        MovieEntity movieEntity = optional.get();
        model.addAttribute("movie", movieEntity);
        model.addAttribute("items", resourceService.search(movieEntity.getTitle(), movieEntity.getDbId(), movieEntity.getImdbId()));
        return "video/movie/resources";
    }

    @PostMapping(path = "/movie/archive")
    @ResponseBody
    public ResponseEntity<VideoStatus> archiveMovie(Long id) {
        Optional<MovieEntity> optional = subjectService.getMovie(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VideoStatus status = videoManager.archive(config.cdn(), config.tmpdir(), optional.get());
        return ResponseEntity.ok(status);
    }

    @RequestMapping("/series/index")
    public String series(Model model) {
        Map<SeriesEntity, List<SeasonEntity>> map = subjectService.listSeries();
        List<SeriesDto> tvs = map.entrySet().stream().map(entry -> {
            SeriesDto seriesDto = new SeriesDto();
            seriesDto.setSeries(entry.getKey());
            List<SeasonDto> seasons = entry.getValue().stream().map(season -> {
                SeasonDto seasonDto = new SeasonDto();
                seasonDto.setId(season.getId());
                seasonDto.setTitle(season.getTitle());
                seasonDto.setDbId(season.getDbId());
                seasonDto.setCurrentSeason(season.getCurrentSeason());
                seasonDto.setArchived(videoManager.getFile(config.cdn(), season).isPresent());
                return seasonDto;
            }).collect(Collectors.toList());
            seriesDto.setSeasons(seasons);
            return seriesDto;
        }).collect(Collectors.toList());
        model.addAttribute("tvs", tvs);
        return "video/series/index";
    }

    @GetMapping(path = "/series/{id}/resources")
    public String seriesResources(@PathVariable Long id, Model model) {
        BiResult<SeriesEntity, List<SeasonEntity>> result = subjectService.getSeries(id);
        if (result.getLeft() == null) {
            return "error/notFound";
        }
        SeriesEntity seriesEntity = result.getLeft();
        model.addAttribute("series", seriesEntity);
        List<SeasonEntity> seasons = result.getRight();
        model.addAttribute("seasons", seasons);
        Set<ResourceItemEntity> items = resourceService.search(seriesEntity.getTitle(), null, seriesEntity.getImdbId());
        seasons.forEach(seasonEntity -> items.addAll(resourceService.search(null, seasonEntity.getDbId(), null)));
        model.addAttribute("items", items);
        return "video/series/resources";
    }

    @PostMapping(path = "/season/archive")
    @ResponseBody
    public ResponseEntity<VideoStatus> archiveSeason(Long id) {
        Optional<SeasonEntity> optional = subjectService.getSeason(id);
        if (optional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        VideoStatus status = videoManager.archive(config.cdn(), config.tmpdir(), optional.get());
        return ResponseEntity.ok(status);
    }
}
