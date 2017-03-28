package polling;


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
