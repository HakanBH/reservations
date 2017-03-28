package search.controllers;

import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import search.exceptions.WrongEmailOrPassword;
import search.model.User;
import search.model.dto.UserLoginDTO;
import search.service.AuthenticationService;
import search.service.persistence.UserService;

import javax.servlet.http.HttpServletResponse;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

/**
 * Created by Hakan_Hyusein on 7/29/2016.
 */
@RestController
@RequestMapping("/")
public class IndexController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private JsonWebTokenUtility tokenService;

    @RequestMapping(method = RequestMethod.GET)
    public ResourceSupport index() {
        ResourceSupport index = new ResourceSupport();
        index.add(linkTo(UserController.class).withRel("users"));
        index.add(linkTo(FacilityOwnerController.class).withRel("owners"));
        index.add(linkTo(FacilityController.class).withRel("facilities"));

        return index;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public User login(@RequestBody UserLoginDTO userLogin, HttpServletResponse response) throws WrongEmailOrPassword {
        return authenticationService.login(userLogin.getEmail(), userLogin.getPassword(), response);
    }
}