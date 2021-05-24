package wsg.tools.internet.movie.omdb;

import java.util.List;
import wsg.tools.internet.base.page.FixedSizePageReq;
import wsg.tools.internet.base.page.FixedSizePageResult;

/**
 * The paged result of movies retrieved from OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
public class OmdbPageResult extends FixedSizePageResult<AbstractOmdbMovie, FixedSizePageReq> {

    OmdbPageResult(List<AbstractOmdbMovie> content, FixedSizePageReq req, long total, int size) {
        super(content, req, total, size);
    }
}
