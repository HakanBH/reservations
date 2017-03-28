package admin.service;

import admin.exceptions.UnauthorizedException;
import admin.exceptions.WrongEmailOrPassword;
import admin.model.RoleType;
import admin.model.User;
import admin.service.persistance.UserService;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hakan_Hyusein on 12/12/2016.
 */
@Service
public class AuthenticationService {

    @Autowired
    private UserService userService;

    @Autowired
    private JsonWebTokenUtility tokenService;

    public User login(String email, String password, HttpServletResponse response) throws WrongEmailOrPassword {
        User user = userService.findByEmailAndPassword(email, password);
        if (user.getRole().getRoleType().equals(RoleType.ROLE_ADMIN)) {
            List<String> roleNames = new ArrayList<>();
            roleNames.add(user.getRole().getRoleType().name());

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
            throw new UnauthorizedException("You're not authorized to see this content.");
        }
    }
}