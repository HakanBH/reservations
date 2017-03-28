package admin.configuration;

import admin.model.*;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

/**
 * Created by Trayan_Muchev on 9/9/2016.
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {

    @Bean
    public JsonWebTokenUtility getTokenUtility() {
        return new JsonWebTokenUtility();
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Reservation.class);
        config.exposeIdsFor(Facility.class);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Picture.class);
        config.exposeIdsFor(UserMessage.class);
    }
}