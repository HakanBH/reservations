package search.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import search.configuration.security.SearchAndRegisterInterceptorHandler;

/**
 * Created by Toncho_Petrov on 10/26/2016.
 */

@Configuration
@ComponentScan(basePackages = "search.configuration")
@EnableWebMvc
public class SearchAndRegisterAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SearchAndRegisterInterceptorHandler());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("PUT", "POST", "GET", "OPTIONS", "PATCH", "DELETE")
                .allowedOrigins("http://epbgsofd0026.budapest.epam.com:3000", "http://localhost:3000", "http://epbgsofd0026:3000", "http://10.22.40.62:3000", " http://epbgsofw0175:3000");
    }
}
