package wsg.tools.internet.info.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.common.jackson.deserializer.CodeEnumDeserializer;
import wsg.tools.common.jackson.deserializer.TitleEnumDeserializer;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.enums.DomesticCity;

/**
 * @author Kingen
 * @see <a href="https://www.scoregg.com/">SCORE</a>
 * @since 2021/3/12
 */
public class ScoreSite extends BaseSite {

    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
        .registerModule(new JavaTimeModule())
        .registerModule(new SimpleModule()
            .addDeserializer(DomesticCity.class, new TitleEnumDeserializer<>(DomesticCity.class))
            .addDeserializer(ScoreStatus.class, CodeEnumDeserializer.intCodeEnum(ScoreStatus.class))
            .addDeserializer(RoundType.class, CodeEnumDeserializer.intCodeEnum(RoundType.class))
        );

    protected ScoreSite() {
        super("Score", new BasicHttpSession("famulei.com"));
    }

    /**
     * Finds paged result of tournaments by the given arguments.
     */
    public TournamentPageResult findAllTournaments(@Nonnull TournamentPageRequest request)
        throws HttpResponseException {
        String type = request.getStatus() == null ? "all" : request.getStatus().getText();
        RequestBuilder builder = create(METHOD_POST, null)
            .setPath("/services/api_url.php")
            .addParameter("api_path", "/services/match/web_tournament_group_list.php")
            .addParameter("method", METHOD_GET)
            .addParameter("platform", "web")
            .addParameter("api_version", "9.9.9")
            .addParameter("language_id", 1)
            .addParameter("gameID", request.getGame().getCode())
            .addParameter("type", type)
            .addParameter("page", request.getCurrent() + 1)
            .addParameter("limit", request.getPageSize());
        if (request.getYear() != null) {
            builder.addParameter("year", request.getYear().getValue());
        }
        TournamentPageResponse response = getObject(builder, MAPPER, TournamentPageResponse.class,
            res -> res.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES || !res.getData()
                .getList().stream().allMatch(t -> t.getStatus() == ScoreStatus.FINISHED));
        if (response.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            throw new HttpResponseException(response.getStatusCode(), response.getMessage());
        }
        TournamentPageResponse.TournamentData data = response.getData();
        return new TournamentPageResult(data.getList(), request, data.getCount());
    }

    /**
     * Finds rounds of a tournament.
     */
    public List<TournamentRound> findRoundsByTournament(@Nonnull Tournament tournament)
        throws HttpResponseException {
        RequestBuilder builder = builder("img1", "/tr/%d.json", tournament.getId());
        SnapshotStrategy<List<TournamentRound>> strategy;
        if (tournament.getStatus() == ScoreStatus.FINISHED) {
            strategy = SnapshotStrategy.never();
        } else {
            strategy = SnapshotStrategy.always();
        }
        return getObject(builder, MAPPER, new TypeReference<>() {
        }, strategy);
    }

    /**
     * Finds match records of a tournament round.
     *
     * @see TournamentRound#getRoundSon()
     */
    public List<MatchRecord> findMatchRecordsByRoundItem(@Nonnull RoundItem item)
        throws HttpResponseException {
        RequestBuilder builder = builder("img1", "/tr_round/%d.json", item.getId());
        return getObject(builder, MAPPER, new TypeReference<>() {
        }, records -> !records.stream().allMatch(r -> r.getStatus() == ScoreStatus.FINISHED));
    }
}
