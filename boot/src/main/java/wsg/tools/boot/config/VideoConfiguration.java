package wsg.tools.boot.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.File;

/**
 * Configurations for video.
 *
 * @author Kingen
 * @since 2020/11/21
 */
@Configuration
@PropertySource("classpath:config/private/video.properties")
public class VideoConfiguration implements InitializingBean {

    @Value("${video.cdn}")
    private String cdn;

    @Value("${video.tmpdir}")
    private String tmpdir;

    public File cdn() {
        return new File(cdn);
    }

    public File tmpdir() {
        return new File(tmpdir);
    }

    @Override
    public void afterPropertiesSet() {
        File file = new File(this.cdn);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a valid cdn: " + cdn);
        }
        file = new File(this.tmpdir);
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("Not a valid tmpdir: " + tmpdir);
        }
    }
}
