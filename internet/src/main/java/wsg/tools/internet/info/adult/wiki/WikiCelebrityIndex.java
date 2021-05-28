package wsg.tools.internet.info.adult.wiki;

import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.SubtypeSupplier;

/**
 * An index pointing to a {@link WikiCelebrity} in the {@link CelebrityWikiSite}.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findAllCelebrityIndices()
 * @see CelebrityWikiSite#findAll(PageIndex, WikiCelebrityType)
 * @since 2021/3/11
 */
public interface WikiCelebrityIndex extends SubtypeSupplier<WikiCelebrityType>, IntIdentifier {

    /**
     * Returns the name of the celebrity.
     *
     * @return the name
     */
    String getName();
}
