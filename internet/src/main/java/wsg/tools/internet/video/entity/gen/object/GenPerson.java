package wsg.tools.internet.video.entity.gen.object;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Person info from PT Gen.
 *
 * @author Kingen
 * @since 2020/8/30
 */
@Getter
@Setter
@JsonIgnoreProperties({"@type"})
public class GenPerson {

    private String name;
    private String url;
}