package wsg.tools.internet.info.adult.wiki;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.PageReq;
import wsg.tools.internet.base.view.SubtypeSupplier;

/**
 * An implementation of {@link PageReq} for {@link CelebrityWikiSite}, with a fixed page size.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageReq extends BasicPageReq implements SubtypeSupplier<WikiCelebrityType> {

    private static final long serialVersionUID = 2881971676332630439L;
    private static final int FIXED_SIZE = 100;

    private final WikiCelebrityType type;

    public WikiPageReq(int current, WikiCelebrityType type) {
        super(current, FIXED_SIZE);
        this.type = type;
    }

    public WikiPageReq(int current) {
        this(current, null);
    }

    @Nonnull
    @Contract(" -> new")
    public static WikiPageReq first() {
        return new WikiPageReq(0);
    }

    @Override
    public WikiPageReq next() {
        return new WikiPageReq(super.next().getCurrent());
    }

    @Override
    public BasicPageReq previous() {
        return new WikiPageReq(super.previous().getCurrent());
    }

    @Override
    public WikiCelebrityType getSubtype() {
        return type;
    }
}
