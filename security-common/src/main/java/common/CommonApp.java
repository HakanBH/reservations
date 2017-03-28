package common;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by Toncho_Petrov on 11/8/2016.
 */

@SpringBootApplication
@EnableWebSecurity
public class CommonApp {

    public static void main(String[] args) {
        SpringApplication.run(CommonApp.class);
    }
}


