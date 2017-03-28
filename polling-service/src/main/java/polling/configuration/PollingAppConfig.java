package polling.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import polling.configuration.broadcast.PollingConsumer;
import polling.configuration.security.PollingInterceptorHandler;

import java.util.List;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */

@Configuration
@ComponentScan("polling.configuration")
@EnableWebMvc
public class PollingAppConfig extends WebMvcConfigurerAdapter {

    @Bean
    public PollingConsumer consumer() {
        return new PollingConsumer();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PollingInterceptorHandler());

    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("PUT", "POST", "GET", "OPTIONS", "PATCH", "DELETE")
                .allowedOrigins("http://epbgsofd0026.budapest.epam.com:3000", "http://localhost:3000", "http://epbgsofd0026:3000", "http://10.22.40.62:3000", " http://epbgsofw0175:3000");
    }

//    @Override
//    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
//        configurer.setDefaultTimeout(30*1000L);
//    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(new MappingJackson2HttpMessageConverter());
    }
}
