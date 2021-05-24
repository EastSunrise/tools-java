package wsg.tools.internet.info.game;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * @author Kingen
 * @see <a href="https://www.scoregg.com/">SCORE</a>
 * @since 2021/3/12
 */
@ConcreteSite
public class ScoreSite extends BaseSite {

    protected ScoreSite() {
        super("Score", httpsHost("famulei.com"));
    }

    /**
     * Finds paged result of tournaments by the given arguments.
     */
    public TournamentPageResult findAllTournaments(@Nonnull TournamentPageReq request)
        throws NotFoundException, OtherResponseException {
        String type = request.getStatus() == null ? "all" : request.getStatus().getAsPath();
        RequestBuilder builder = RequestBuilder.post("/services/api_url.php")
            .addParameter("api_path", "/services/match/web_tournament_group_list.php")
            .addParameter("method", "get")
            .addParameter("platform", "web")
            .addParameter("api_version", "9.9.9")
            .addParameter("language_id", String.valueOf(1))
            .addParameter("gameID", String.valueOf(request.getGame().getCode()))
            .addParameter("type", type)
            .addParameter("page", String.valueOf(request.getCurrent() + 1))
            .addParameter("limit", String.valueOf(request.getPageSize()));
        if (request.getYear() != null) {
            builder.addParameter("year", String.valueOf(request.getYear().getValue()));
        }
        TournamentPageResponse response = getObject(builder, Lazy.MAPPER,
            TournamentPageResponse.class);
        if (response.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
            if (response.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(response.getMessage());
            }
            throw new OtherResponseException(response.getStatusCode(), response.getMessage());
        }
        TournamentPageResponse.TournamentData data = response.getData();
        return new TournamentPageResult(data.getList(), request, data.getCount());
    }

    /**
     * Retrieves rounds of a tournament.
     */
    public List<TournamentRound> findRoundsByTournament(@Nonnull Tournament tournament)
        throws NotFoundException, OtherResponseException {
        return getObject(create("img1", METHOD_GET, "/tr/%d.json", tournament.getId()),
            Lazy.MAPPER, new TypeReference<>() {
            });
    }

    /**
     * Retrieves match records of a tournament round.
     *
     * @see TournamentRound#getRoundSon()
     */
    public List<MatchRecord> findMatchRecordsByRoundItem(@Nonnull RoundItem item)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = create("img1", METHOD_GET, "/tr_round/%d.json", item.getId());
        return getObject(builder, Lazy.MAPPER, new TypeReference<>() {
        });
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .registerModule(new JavaTimeModule())
            .registerModule(new SimpleModule()
                .addDeserializer(ScoreStatus.class, EnumDeserializers.ofIntCode(ScoreStatus.class))
                .addDeserializer(RoundType.class, EnumDeserializers.ofIntCode(RoundType.class))
            );
    }
}
