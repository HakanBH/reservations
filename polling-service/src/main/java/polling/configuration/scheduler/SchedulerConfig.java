package polling.configuration.scheduler;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by Toncho_Petrov on 1/16/2017.
 */

@Configuration
@EnableScheduling
@ComponentScan(basePackages = "polling.controllers")
public class SchedulerConfig {
}
