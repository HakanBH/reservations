package search.service;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import search.exceptions.UnauthorizedException;
import search.exceptions.WrongEmailOrPassword;
import search.model.User;
import search.service.persistence.UserService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan_Hyusein on 12/15/2016.
 */
@Service
public class AuthenticationService {

    @Autowired
    UserService userService;

    @Autowired
    private JsonWebTokenUtility tokenService;

    public User login(String email, String password, HttpServletResponse response) throws WrongEmailOrPassword {
        User user = userService.findByEmailAndPassword(email, password);
        if (user == null || user.isDeleted()) {
            throw new WrongEmailOrPassword("User with this email or password does not exists");
        } else {
            if (user.getVerified() == Boolean.TRUE) {
                List<String> roleNames = new ArrayList<>();
                roleNames.add(user.getRole().getRoleType().name());
                // Build the AuthTokenDetailsDTO
                AuthTokenDetailsDTO authTokenDetailsDTO = new AuthTokenDetailsDTO();
                authTokenDetailsDTO.userId = user.getId().toString();
                authTokenDetailsDTO.email = user.getEmail();
                authTokenDetailsDTO.roleNames = roleNames;
                authTokenDetailsDTO.expirationDate = tokenService.buildExpirationDate();

                // Create auth token
                String jwt = tokenService.createJsonWebToken(authTokenDetailsDTO);
                if (jwt != null) {
                    response.addHeader("Authorization", jwt);
                    response.addHeader("Access-Control-Expose-Headers", "Authorization");
                }
                return user;
            } else {
                throw new UnauthorizedException("Current user is not verified");
            }
        }
    }
}