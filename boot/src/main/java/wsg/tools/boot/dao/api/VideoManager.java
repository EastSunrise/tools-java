package wsg.tools.boot.dao.api;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import wsg.tools.boot.pojo.dto.SubjectDto;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;

/**
 * Management of local videos.
 *
 * @author Kingen
 * @since 2020/7/17
 */
@Component
@PropertySource("classpath:config/private/video.properties")
public class VideoManager {

    @Getter
    private final String cdn;

    public VideoManager(@Value("${cdn}") String cdn) {
        File file = new File(cdn);
        if (!file.isDirectory()) {
            throw new RuntimeException(new FileNotFoundException("Cdn doesn't exist."));
        }
        this.cdn = cdn;
    }

    /**
     * Returns location of the given subject.
     * todo
     *
     * @return null if not exist
     */
    public String location(SubjectDto subject) {
        Objects.requireNonNull(subject);
        Objects.requireNonNull(subject.getLanguages());
        return null;
    }
}
