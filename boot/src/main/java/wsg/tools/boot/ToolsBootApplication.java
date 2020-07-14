package wsg.tools.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Application startup
 *
 * @author Kingen
 */
@SpringBootApplication
@EnableJpaRepositories("wsg.tools.boot.dao.jpa.mapper")
public class ToolsBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsBootApplication.class, args);
    }

}
