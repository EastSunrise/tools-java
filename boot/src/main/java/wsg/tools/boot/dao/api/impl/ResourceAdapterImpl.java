package wsg.tools.boot.dao.api.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import wsg.tools.boot.dao.api.intf.ResourceAdapter;
import wsg.tools.boot.pojo.entity.MovieEntity;
import wsg.tools.boot.pojo.entity.SeasonEntity;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.download.Downloader;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.BaseItem;
import wsg.tools.internet.resource.entity.item.BdFilmItem;
import wsg.tools.internet.resource.entity.item.SimpleItem;
import wsg.tools.internet.resource.entity.item.Y80sItem;
import wsg.tools.internet.resource.entity.resource.base.BaseResource;
import wsg.tools.internet.resource.site.BdFilmSite;
import wsg.tools.internet.resource.site.MovieHeavenSite;
import wsg.tools.internet.resource.site.XlcSite;
import wsg.tools.internet.resource.site.Y80sSite;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Kingen
 * @since 2020/11/3
 */
@Slf4j
@Configuration
public class ResourceAdapterImpl implements ResourceAdapter {

    private static final Pattern POSSIBLE_TITLE_REGEX =
            Pattern.compile("(\\[[^\\[\\]]+])?(?<title>[^\\[\\]]+)(\\[[^\\[\\]]+]([^\\[\\]]+)?)?");

    private static final Set<VideoType> MOVIE_TYPES = Set.of(
            VideoType.FHD, VideoType.FOUR_K, VideoType.HD, VideoType.MOVIE, VideoType.THREE_D
    );
    private static final Set<VideoType> SERIES_TYPES = Set.of(VideoType.TV, VideoType.VARIETY);
    private final Y80sSite y80sSite = new Y80sSite();
    private final XlcSite xlcSite = new XlcSite();
    private final BdFilmSite bdFilmSite = new BdFilmSite();
    private final MovieHeavenSite movieHeavenSite = new MovieHeavenSite();
    private final Downloader downloader = new Thunder();

    @Override
    public long download(MovieEntity movie, File target) {
        int year = movie.getYear().getValue();

        Set<BaseResource> resources = new HashSet<>(collect(bdFilmItem(movie)));

        Predicate<SimpleItem> predicate = testSimpleItem(year, MOVIE_TYPES::contains, testMovieTitle(movie.getTitle(), year));
        Stream<SimpleItem> movieHeavenStream = movieHeavenSite.search(movie.getTitle()).stream().filter(predicate);
        resources.addAll(collect(movieHeavenStream));
        Stream<SimpleItem> xlcStream = xlcSite.search(movie.getTitle()).stream().filter(predicate);
        resources.addAll(collect(xlcStream));

        Predicate<Y80sItem> y80sItemPredicate = testY80sItem(
                testSimpleItem(year, MOVIE_TYPES::contains, simplifyMovieTitle(movie.getTitle(), year)), movie.getDbId()
        );
        Stream<Y80sItem> y80sStream = y80sSite.search(movie.getTitle()).stream().filter(y80sItemPredicate);
        resources.addAll(collect(y80sStream));

        long count = resources.stream().filter(resource -> {
            try {
                return downloader.addTask(target, resource);
            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
        }).count();
        log.info("{} resources added to download.", count);
        return count;
    }

    @Override
    public long download(SeasonEntity season, File target) {
        int year = season.getYear().getValue();
        String title = season.getSeries().getTitle();

        Predicate<SimpleItem> predicate = testSimpleItem(year, SERIES_TYPES::contains,
                testSeriesTitle(title, year, season.getCurrentSeason()));
        Stream<SimpleItem> movieHeavenStream = movieHeavenSite.search(title).stream().filter(predicate);
        Set<BaseResource> resources = new HashSet<>(collect(movieHeavenStream));
        Stream<SimpleItem> xlcStream = xlcSite.search(title).stream().filter(predicate);
        resources.addAll(collect(xlcStream));
        Stream<Y80sItem> y80sStream = y80sSite.search(title).stream().filter(testY80sItem(predicate, season.getDbId()));
        resources.addAll(collect(y80sStream));

        long count = resources.stream().filter(resource -> {
            try {
                return downloader.addTask(target, resource);
            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
        }).count();
        log.info("{} resources added to download.", count);
        return count;
    }

    private Stream<BdFilmItem> bdFilmItem(MovieEntity movie) {
        Set<BdFilmItem> items = new HashSet<>();
        items.addAll(bdFilmSite.search(movie.getTitle()));
        items.addAll(bdFilmSite.search(movie.getImdbId()));
        items.addAll(bdFilmSite.search(String.valueOf(movie.getDbId())));
        return items.stream().filter(item -> {
            if (item.getImdbId() != null && movie.getImdbId().equals(item.getImdbId())) {
                return true;
            }
            return item.getDbId() != null && movie.getDbId().equals(item.getDbId());
        });
    }

    private Predicate<Y80sItem> testY80sItem(Predicate<SimpleItem> simpleItemPredicate, final Long dbId) {
        return item -> {
            if (item.getDbId() == null) {
                return simpleItemPredicate.test(item);
            }
            return Objects.equals(dbId, item.getDbId());
        };
    }

    /**
     * Filter {@link SimpleItem}.
     */
    private Predicate<SimpleItem> testSimpleItem(
            final int year, Predicate<VideoType> typePredicate, Predicate<String> titlePredicate) {
        return item -> {
            if (item.getYear() == null || item.getYear() != year) {
                return false;
            }
            if (!typePredicate.test(item.getType())) {
                return false;
            }
            return titlePredicate.test(item.getTitle());
        };
    }

    private Predicate<String> simplifyMovieTitle(final String target, int year) {
        return (provided) -> {
            Matcher matcher = RegexUtils.matchesOrElseThrow(POSSIBLE_TITLE_REGEX, provided);
            provided = matcher.group("title");
            return testMovieTitle(target, year).test(provided);
        };
    }

    private Predicate<String> testMovieTitle(final String target, int year) {
        return (provided) -> {
            List<String> possibles = Arrays.asList(
                    target,
                    target + "I", target + "1",
                    target + year, target + "[" + year + "]", String.format("%s%02d", target, year % 100),
                    target + " 加长版", target + "[DVD版]", target + "(未删减完整版)", target + "完整版",
                    target + "3D", target + "大电影", target + "3D版", target + "纪录片",
                    target + "国粤双语中字", target + "高清", target + "加长"
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        };
    }

    private Predicate<String> testSeriesTitle(final String target, final int year, final int season) {
        return (provided) -> {
            if (season == 1) {
                List<String> possibles = Arrays.asList(
                        target,
                        target + "第一季", target + " 第一季", target + "[第一季]",
                        target + "第一部", target + " 第一部", target + "[第一部]",
                        target + year, target + "[" + year + "]", String.format("%02d版%s", year % 100, target),
                        target + "[纪录片]", target + "电视版", target + "[DVD]版", target + "D", target + "(电视剧)"
                );
                return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
            }

            String chineseNumeric = StringUtilsExt.chineseNumeric(season);
            String seasonStr = String.format("第%s季", chineseNumeric);
            String seasonStr2 = String.format("第%s部", chineseNumeric);
            List<String> inits = Arrays.asList(
                    target + season, target + chineseNumeric,
                    target + seasonStr, target + " " + seasonStr, target + "[" + seasonStr + "]",
                    target + seasonStr2, target + " " + seasonStr2, target + "[" + seasonStr2 + "]"
            );
            List<String> possibles = new ArrayList<>();
            inits.forEach(s -> {
                possibles.add(s);
                possibles.add(s + "[未删版]");
                possibles.add(s + "[未删]");
            });
            String[] parts = provided.split("/");
            if (provided.endsWith(seasonStr)) {
                for (int i = 0; i < parts.length - 1; i++) {
                    parts[i] = parts[i] + seasonStr;
                }
            }
            return Arrays.stream(parts).anyMatch(possibles::contains);
        };
    }

    private Set<BaseResource> collect(Stream<? extends BaseItem> stream) {
        Set<BaseResource> resources = new HashSet<>();
        stream.forEach(item -> item.getResources().forEach(resource -> {
            if (resource instanceof BaseResource) {
                resources.add((BaseResource) resource);
            }
        }));
        return resources;
    }
}
