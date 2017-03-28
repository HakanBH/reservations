package polling.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import polling.model.Facility;
import polling.model.Picture;
import polling.model.Reservation;
import polling.model.User;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
@Configuration
public class RepositoryConfig extends RepositoryRestConfigurerAdapter {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Reservation.class);
        config.exposeIdsFor(Facility.class);
        config.exposeIdsFor(User.class);
        config.exposeIdsFor(Picture.class);
    }
}