package wsg.tools.internet.resource.entity.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.site.Y80sSite;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;

import javax.annotation.Nonnull;

/**
 * Items of {@link Y80sSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
@Getter
@Setter
public class Y80sItem extends SimpleItem implements DoubanIdentifier {

    private Long dbId;

    public Y80sItem(@Nonnull String url) {
        super(url);
    }
}
