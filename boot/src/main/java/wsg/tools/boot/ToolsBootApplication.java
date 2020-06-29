package wsg.tools.boot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Application startup
 *
 * @author Kingen
 */
@SpringBootApplication
@MapperScan("wsg.tools.boot.dao.mapper")
public class ToolsBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(ToolsBootApplication.class, args);
    }

}
