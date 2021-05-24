package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.http.client.methods.RequestBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.repository.RepoPageable;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
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
        super("RR TV", httpHost("rr.tv"));
    }

    /**
     * Only first 10 pages are available regardless of arguments of the request.
     */
    @Nonnull
    @Override
    public RenrenPageResult findPage(@Nonnull RenrenPageReq req)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = create("content.json", METHOD_GET,
            "/morpheus/filter/%s/%s/%s/all/%s/%s/%d",
            Optional.ofNullable(req.getType()).map(Object::toString).orElse("all"),
            Optional.ofNullable(req.getArea()).map(Object::toString).orElse("all"),
            Optional.ofNullable(req.getGenre()).map(Object::toString).orElse("all"),
            Optional.ofNullable(req.getStatus()).map(Object::toString).orElse("all"),
            req.getSort(), req.getCurrent() + 1);
        RenrenResponse response = getObject(builder, Lazy.MAPPER, RenrenResponse.class);
        RenrenResponse.Result result = response.getResult();
        return new RenrenPageResult(result.getContent(), req, result.isEnd());
    }

    @Override
    @Nonnull
    public RenrenSeries findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        Document document = getDocument(create("m", METHOD_GET, "/detail/%d", id));
        Element info = document.selectFirst(".info");
        String title = info.selectFirst(".title-text").text().strip();
        String description = info.selectFirst(".desc").text().strip();
        double score = Double.parseDouble(info.selectFirst(".scroce").text());
        String[] parts = info.selectFirst(".type-area-cagegory").text().split("/");
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.YEAR_REGEX, parts[0]);
        int year = Integer.parseInt(matcher.group("y"));
        List<Region> regions = new ArrayList<>(1);
        List<MovieGenre> genres = new ArrayList<>(2);
        regions.add(EnumUtilExt.valueOfAlias(Region.class, parts[1]));
        boolean isGenre = false;
        for (int i = 2; i < parts.length; i++) {
            String part = parts[i];
            if (isGenre) {
                genres.add(EnumUtilExt.valueOfAlias(MovieGenre.class, part));
            } else {
                try {
                    genres.add(EnumUtilExt.valueOfAlias(MovieGenre.class, part));
                    isGenre = true;
                } catch (IllegalArgumentException e) {
                    regions.add(EnumUtilExt.valueOfAlias(Region.class, part));
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
            .map(s -> SeriesStatus.CONCLUDED.getDisplayName().equals(s))
            .map(flag -> flag ? SeriesStatus.CONCLUDED : SeriesStatus.AIRING)
            .orElse(null);
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new SimpleModule()
                .addDeserializer(MovieGenre.class, EnumDeserializers.ofAlias(MovieGenre.class)));
        private static final Pattern YEAR_REGEX = Pattern
            .compile("(?<y>\\d{4})(?:-\\d{2}-\\d{2})?");
    }
}
