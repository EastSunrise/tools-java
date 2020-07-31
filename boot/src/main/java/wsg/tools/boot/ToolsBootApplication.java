package wsg.tools.boot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import wsg.tools.boot.dao.jpa.base.BaseRepositoryImpl;

/**
 * Application startup
 *
 * @author Kingen
 */
@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class ToolsBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsBootApplication.class, args);
    }

}
