package wsg.tools.internet.video.site.imdb;

/**
 * Base item of IMDb.
 *
 * @author Kingen
 * @since 2021/2/12
 */
class BaseImdbTitle implements ImdbIdentifier {

    private String imdbId;

    BaseImdbTitle() {
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }

    void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}
