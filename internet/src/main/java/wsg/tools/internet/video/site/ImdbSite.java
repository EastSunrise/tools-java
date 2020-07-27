package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.http.client.HttpResponseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.imdb.ImdbSubject;
import wsg.tools.internet.video.enums.ImdbTypeEnum;

import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * <a href="https://imdb.com">IMDb</a>
 *
 * @author Kingen
 * @since 2020/6/16
 */
public final class ImdbSite extends BaseSite {

    private static final Pattern MOVIE_TITLE_REGEX = Pattern.compile("([^()]+) \\((TV Movie )?(\\d{4})\\) - IMDb");
    private static final Pattern TV_TITLE_REGEX = Pattern.compile("([^()]+) \\(TV ((Mini-)?Series|Episode) (\\d{4})(–(\\d{4}|\\s))?\\) - IMDb");
    private static final Pattern VIDEO_TITLE_REGEX = Pattern.compile("([^()]+) \\((Video )?(\\d{4})\\) - IMDb");

    public ImdbSite() {
        super("IMDb", "imdb.com", 1);
    }

    /**
     * Get subject info by parsing the html page
     */
    public ImdbSubject title(String tt) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildPath("/title/%s", tt).build());
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        ImdbSubject subject = new ImdbSubject();
        subject.setImdbId(tt);

        Element head = document.selectFirst("head");
        JsonNode root;
        try {
            root = getObjectMapper().readTree(head.selectFirst("script[type=application/ld+json]").html());
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        ImdbTypeEnum type = EnumUtilExt.deserializeAka(root.get("@type").textValue(), ImdbTypeEnum.class);
        subject.setType(type);

        return subject;
    }
}
