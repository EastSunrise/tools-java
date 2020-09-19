package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Detail of title with id of Douban.
 *
 * @author Kingen
 * @since 2020/9/10
 */
@Getter
@Setter
public class IdentifiedTitleDetail extends TitleDetail {

    private Long dbId;
}
