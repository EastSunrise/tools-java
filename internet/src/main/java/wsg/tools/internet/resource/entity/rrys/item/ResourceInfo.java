package wsg.tools.internet.resource.entity.rrys.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.common.ChannelEnum;

import java.time.LocalDateTime;

/**
 * Details of a resource.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class ResourceInfo extends ResourceItem {

    private String showType;
    private String aliasname;
    private LocalDateTime expire;
    private ChannelEnum channel;
    private String channelCn;
    private Integer views;
}
