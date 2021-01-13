package wsg.tools.internet.resource.entity.item.impl;

import lombok.Setter;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.site.impl.XlmSite;

import javax.annotation.Nonnull;

/**
 * Items of {@link XlmSite}.
 *
 * @author Kingen
 * @since 2021/1/9
 */
@Setter
public class XlmItem extends IdentifiedItem implements Identifier<Integer>, TypeSupplier {

    private VideoType type;

    public XlmItem(int id, @Nonnull String url) {
        super(id, url);
    }

    @Override
    public VideoType getType() {
        return type;
    }
}
