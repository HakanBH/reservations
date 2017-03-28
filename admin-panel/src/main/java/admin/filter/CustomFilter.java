package admin.filter;

import admin.configuration.AllowedHostsConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Hakan_Hyusein on 12/12/2016.
 */
@Configuration
public class CustomFilter extends OncePerRequestFilter {

    @Autowired
    private AllowedHostsConfiguration allowedHostsConfiguration;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (allowedHostsConfiguration.getHosts().contains(request.getRemoteHost())) {
            filterChain.doFilter(request, response);
        } else {
            response.sendError(HttpStatus.UNAUTHORIZED.value(), "You're not authorized too see this content.");
        }
    }
}
