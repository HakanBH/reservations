package polling.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import polling.PollingApp;

import static org.junit.Assert.*;

/**
 * Created by Toncho_Petrov on 1/12/2017.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = PollingApp.class)
@WebAppConfiguration
@Transactional
@ComponentScan("reservations.controllers")
@IntegrationTest({"server.port=0", "management.port=0", "spring.profiles.active=test"})
public class PollingControllerTest {


    @Test
    public void polling() throws Exception {

    }

}