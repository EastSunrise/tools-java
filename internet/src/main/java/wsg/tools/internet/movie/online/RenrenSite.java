package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.jackson.deserializer.TitleEnumDeserializer;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BasicHttpSession;
import wsg.tools.internet.base.support.RequestBuilder;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.Scheme;
import wsg.tools.internet.common.enums.Region;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * @author Kingen
 * @see <a href="http://m.rr.tv/">Renren Video</a>
 * @since 2021/3/22
 */
public class RenrenSite extends BaseSite implements RepoRetrievable<Integer, RenrenSeries>,
    RepoPageable<RenrenPageReq, RenrenPageResult> {

    public RenrenSite() {
        super("RR TV", new BasicHttpSession(Scheme.HTTP, "rr.tv"));
    }

    /**
     * Only first 10 pages are available regardless of arguments of the request.
     */
    @Nonnull
    @Override
    public RenrenPageResult findPage(@Nonnull RenrenPageReq request)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder("content.json",
            "/morpheus/filter/%s/%s/%s/all/%s/%s/%d",
            Optional.ofNullable(request.getType()).map(Object::toString).orElse("all"),
            Optional.ofNullable(request.getArea()).map(Object::toString).orElse("all"),
            Optional.ofNullable(request.getGenre()).map(Object::toString).orElse("all"),
            Optional.ofNullable(request.getStatus()).map(Object::toString).orElse("all"),
            request.getSort(), request.getCurrent() + 1);
        RenrenResponse response = getObject(builder, Lazy.MAPPER, RenrenResponse.class, t -> true);
        RenrenResponse.Result result = response.getResult();
        return new RenrenPageResult(result.getContent(), request, result.isEnd());
    }

    @Override
    @Nonnull
    public RenrenSeries findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = builder("m", "/detail/%d", id);
        Document document = getDocument(builder, doc -> {
            SeriesStatus status = getStatus(doc);
            return status != null && status != SeriesStatus.CONCLUDED;
        });
        Element info = document.selectFirst(".info");
        String title = info.selectFirst(".title-text").text().strip();
        String description = info.selectFirst(".desc").text().strip();
        double score = Double.parseDouble(info.selectFirst(".scroce").text());
        String[] parts = info.selectFirst(".type-area-cagegory").text().split("/");
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.YEAR_REGEX, parts[0]);
        int year = Integer.parseInt(matcher.group("y"));
        List<Region> regions = new ArrayList<>(1);
        List<MovieGenre> genres = new ArrayList<>(2);
        regions.add(EnumUtilExt.valueOfAka(Region.class, parts[1]));
        boolean isGenre = false;
        for (int i = 2; i < parts.length; i++) {
            String part = parts[i];
            if (isGenre) {
                genres.add(EnumUtilExt.valueOfTitle(MovieGenre.class, part, false));
            } else {
                try {
                    genres.add(EnumUtilExt.valueOfTitle(MovieGenre.class, part, false));
                    isGenre = true;
                } catch (IllegalArgumentException e) {
                    regions.add(EnumUtilExt.valueOfAka(Region.class, part));
                }
            }
        }
        int eps = document.selectFirst(".episode-list").children().size();
        RenrenSeries series = new RenrenSeries(
            id, title, description, score, year, regions, genres, eps);
        series.setStatus(getStatus(document));
        return series;
    }

    private SeriesStatus getStatus(@Nonnull Document document) {
        return Optional.ofNullable(document.selectFirst(".playDateInfo")).map(Element::text)
            .map(s -> SeriesStatus.CONCLUDED.getTitle().equals(s))
            .map(flag -> flag ? SeriesStatus.CONCLUDED : SeriesStatus.AIRING)
            .orElse(null);
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new SimpleModule()
                .addDeserializer(MovieGenre.class, new TitleEnumDeserializer<>(MovieGenre.class)));
        private static final Pattern YEAR_REGEX = Pattern
            .compile("(?<y>\\d{4})(?:-\\d{2}-\\d{2})?");
    }
}
