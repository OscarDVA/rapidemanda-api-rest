package pe.gob.pj.rapidemanda.infraestructure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(basePackages = {"pe.gob.pj.rapidemanda.*"})
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class SpringBootInit extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootInit.class, args);
    }

}