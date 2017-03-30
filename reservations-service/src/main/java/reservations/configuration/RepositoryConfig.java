package reservations.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import reservations.model.Facility;
import reservations.model.Picture;
import reservations.model.Reservation;
import reservations.model.User;

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
