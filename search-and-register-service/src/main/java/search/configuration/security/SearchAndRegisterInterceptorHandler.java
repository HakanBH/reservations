package search.configuration.security;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import io.jsonwebtoken.JwtException;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Toncho_Petrov on 10/24/2016.
 */
@ComponentScan(basePackages = "search.configuration.security")
public class SearchAndRegisterInterceptorHandler extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String token = request.getHeader("Authorization");

        JsonWebTokenUtility tokenUtility = new JsonWebTokenUtility();
        AuthTokenDetailsDTO detailsDTO = null;
        if (request.getRequestURI().equals("/facebook") || request.getRequestURI().equals("/google")) {
            response.addHeader("Access-Control-Allow-Headers", "token");
        }
        if (token != null) {
            detailsDTO = tokenUtility.parseAndValidate(token);
            if (detailsDTO == null) {
                throw new AccessDeniedException("Access denied.");
            }
            if (tokenUtility.isExpiration(detailsDTO.expirationDate)) {
                throw new JwtException("Token's expired.");
            }
        }
        response.addHeader("Authorization", token);
        response.addHeader("Access-Control-Expose-Headers", "Authorization");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        String token = request.getHeader("Authorization");
        response.addHeader("Authorization", token);
//        response.addHeader("Access-Control-Expose-Headers", "Authorization");
    }
}
