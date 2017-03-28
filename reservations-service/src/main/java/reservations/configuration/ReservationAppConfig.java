package reservations.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;
import reservations.configuration.security.ReservationInterceptorHandler;

import java.util.List;

/**
 * Created by Toncho_Petrov on 10/19/2016.
 */

@Configuration
@ComponentScan({"reservations.configuration"})
@EnableWebMvc
public class ReservationAppConfig extends WebMvcConfigurerAdapter {


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new ReservationInterceptorHandler());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("PUT", "POST", "GET", "OPTIONS", "PATCH", "DELETE")
                .allowedOrigins("http://epbgsofd0026.budapest.epam.com:3000", "http://localhost:3000", "http://epbgsofd0026:3000", "http://10.22.40.62:3000", " http://epbgsofw0175:3000");
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(30 * 1000L);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }

}
