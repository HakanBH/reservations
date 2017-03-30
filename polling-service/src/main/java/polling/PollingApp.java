package polling;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.hateoas.config.EnableHypermediaSupport;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableHypermediaSupport(type = EnableHypermediaSupport.HypermediaType.HAL)
public class PollingApp {


    public static void main(String[] args) throws IOException, TimeoutException {
        SpringApplication.run(PollingApp.class);
    }
}
