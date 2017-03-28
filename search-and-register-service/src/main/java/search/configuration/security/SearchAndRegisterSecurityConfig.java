package search.configuration.security;

import common.jsonwebtoken.JsonWebTokenUtility;
import common.security.JsonWebTokenSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

/**
 * Created by Toncho_Petrov on 10/5/2016.
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "common.security")
public class SearchAndRegisterSecurityConfig extends JsonWebTokenSecurityConfig {

    @Bean
    public JsonWebTokenUtility getTokenUtility() {
        return new JsonWebTokenUtility();
    }


    @Override
    protected void setupAuthorization(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // allow anonymous access to /authenticate endpoint
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/login").permitAll()
                .antMatchers(HttpMethod.GET, "/facilities/picture/**").permitAll()
                .antMatchers("/users/profilePic/*").permitAll()
                .antMatchers(HttpMethod.POST, "/contactUs").permitAll()
                .antMatchers(HttpMethod.POST, "/users/*/imageUpload").permitAll()
                .antMatchers(HttpMethod.GET, "/reservations").permitAll()
                .antMatchers(HttpMethod.PUT, "/forgottenPass").permitAll()
                .antMatchers(HttpMethod.POST, "/facebook").permitAll()
                .antMatchers(HttpMethod.POST, "/google").permitAll()
                .antMatchers(HttpMethod.PUT, "/users/forgottenPass").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/facilities").permitAll()
                .antMatchers(HttpMethod.POST, "/owners").permitAll()
                .anyRequest().authenticated();
    }
}
