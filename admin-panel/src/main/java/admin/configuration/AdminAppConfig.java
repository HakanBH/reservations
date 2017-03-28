package admin.configuration;

import admin.configuration.security.AdminHandlerInterceptor;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by Toncho_Petrov on 10/27/2016.
 */
@Configuration
@ComponentScan("admin.configuration")
@EnableWebMvc
public class AdminAppConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminHandlerInterceptor());
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("PUT", "POST", "GET", "OPTIONS", "PATCH", "DELETE")
                .allowedOrigins("http://epbgsofd0026.budapest.epam.com:3000", "http://localhost:3000", "http://epbgsofd0026:3000", "http://10.22.40.62:3000");
    }
}