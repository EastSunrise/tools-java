package wsg.tools.internet.movie.omdb;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.lang.AssertUtils;

/**
 * The request with required arguments when accessing to the OMDb API. At least ane argument is
 * required.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbReq {

    private final String imdbId;
    private final String title;

    OmdbReq(String imdbId, String title) {
        this.imdbId = imdbId;
        this.title = title;
    }

    @Nonnull
    @Contract("_ -> new")
    public static OmdbReq ofId(String imdbId) {
        return new OmdbReq(AssertUtils.requireNotBlank(imdbId), null);
    }

    @Nonnull
    @Contract("_ -> new")
    public static OmdbReq ofTitle(String title) {
        return new OmdbReq(null, AssertUtils.requireNotBlank(title));
    }

    @Nonnull
    @Contract("_, _ -> new")
    public static OmdbReq ofIdAndTitle(String imdbId, String title) {
        AssertUtils.requireNotBlank(imdbId);
        AssertUtils.requireNotBlank(title);
        return new OmdbReq(imdbId, title);
    }

    public String getImdbId() {
        return imdbId;
    }

    public String getTitle() {
        return title;
    }
}
