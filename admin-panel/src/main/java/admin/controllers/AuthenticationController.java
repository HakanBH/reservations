package admin.controllers;

import admin.exceptions.UnauthorizedException;
import admin.exceptions.WrongEmailOrPassword;
import admin.model.User;
import admin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by Hakan_Hyusein on 12/9/2016.
 */
@RestController
@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            return authenticationService.login(credentials.get("email"), credentials.get("password"), response);
        } catch (WrongEmailOrPassword e) {
            throw new UnauthorizedException("You're not authorized to see this content");
        }
    }
}
