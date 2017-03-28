package polling.configuration.security;

import common.jsonwebtoken.JsonWebTokenUtility;
import common.security.JsonWebTokenSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Component;

/**
 * Created by Toncho_Petrov on 10/18/2016.
 */

@Component
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@ComponentScan(basePackages = "common.security")
public class PollingSecurityConfig extends JsonWebTokenSecurityConfig {

    @Bean
    public JsonWebTokenUtility getTokenUtility() {
        return new JsonWebTokenUtility();
    }

    @Override
    protected void setupAuthorization(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                // allow anonymous access to /authenticate endpoint
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .antMatchers(HttpMethod.GET,"/reservations").permitAll()
//                .antMatchers(HttpMethod.GET,"/gs-guide-websocket/**").hasAnyAuthority("ROLE_USER","ROLE_FACILITY_OWNER","ROLE_ADMIN")
                .antMatchers(HttpMethod.GET, "/gs-guide-websocket/**").permitAll()

                .anyRequest().authenticated();
    }
}