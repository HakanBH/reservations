package search.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.Google;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import search.exceptions.ExistingUserException;
import search.model.Role;
import search.model.RoleType;
import search.model.User;
import search.service.persistence.UserService;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Toncho_Petrov on 9/9/2016.
 */
@RestController
public class SocialController {

    private List<String> roleNames = new ArrayList<>();
    private AuthTokenDetailsDTO authTokenDetailsDTO = null;
    private String jwt = null;
    private Authentication authentication;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/facebook", method = RequestMethod.POST)
    public ResponseEntity facebookLogin(@RequestHeader("token") String token, HttpServletResponse response) throws ExistingUserException {

        Facebook facebook = new FacebookTemplate(token);
        User user = null;

        if (facebook.userOperations().getUserProfile().getEmail() != null) {
            user = userService.findByFacebookEmail(facebook.userOperations().getUserProfile().getEmail());
            if (user != null) {
                roleNames.add(user.getRole().getRoleType().name());
                // Build the AuthTokenDetailsDTO
                AuthTokenDetailsDTO authTokenDetailsDTO = new AuthTokenDetailsDTO();
                authTokenDetailsDTO.userId = user.getId().toString();
                authTokenDetailsDTO.email = user.getEmail();
                authTokenDetailsDTO.roleNames = roleNames;
                authTokenDetailsDTO.expirationDate = tokenUtility.buildExpirationDate();

                user.setAccountNonExpired(true);
                user.setAccountNonLocked(true);
                user.setCredentialsNonExpired(true);
                user.setEnabled(true);

                // Create auth token
                jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
                if (jwt != null) {
                    response.addHeader("Authorization", jwt);
                    response.addHeader("Access-Control-Expose-Headers", "Authorization");
                }
                authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                user = new User();
                user.setEmail(facebook.userOperations().getUserProfile().getId() + "@facebook.com");
                user.setFirstName(facebook.userOperations().getUserProfile().getFirstName());
                user.setLastName(facebook.userOperations().getUserProfile().getLastName());
                user.setPassword("1234");
                Role role = new Role();
                role.setRoleType(RoleType.ROLE_USER);
                user.setRole(role);
                user.setFacebookEmail(facebook.userOperations().getUserProfile().getEmail());
                user.setFacebookId(facebook.userOperations().getUserProfile().getId());
                user.setFacebookPic("http://graph.facebook.com/" + facebook.userOperations().getUserProfile().getId() + "/picture?type=large");
                userService.save(user);

                roleNames.clear();
                roleNames.add(user.getRole().getRoleType().name());
                // Build the AuthTokenDetailsDTO
                authTokenDetailsDTO = new AuthTokenDetailsDTO();
                authTokenDetailsDTO.userId = "" + user.getId();
                authTokenDetailsDTO.email = user.getEmail();
                authTokenDetailsDTO.roleNames = roleNames;
                authTokenDetailsDTO.expirationDate = buildExpirationDate();

                // Create auth token
                jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
                if (jwt != null) {
                    response.addHeader("Authorization", jwt);
                    response.addHeader("Access-Control-Expose-Headers", "Authorization");
                }

                authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.status(HttpStatus.OK).body(user);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @RequestMapping(value = "/google", method = RequestMethod.POST)
    public ResponseEntity googleLogin(@RequestHeader("token") String token, HttpServletResponse response) throws ExistingUserException {

        Google google = new GoogleTemplate(token);
        User user = null;
        if (google.userOperations().getUserInfo().getEmail() != null) {
            user = userService.findByGoogleEmail(google.userOperations().getUserInfo().getEmail());
            if (user != null) {
                roleNames.clear();
                roleNames.add(user.getRole().getRoleType().name());
                // Build the AuthTokenDetailsDTO
                authTokenDetailsDTO = new AuthTokenDetailsDTO();
                authTokenDetailsDTO.userId = "" + user.getId();
                authTokenDetailsDTO.email = user.getEmail();
                authTokenDetailsDTO.roleNames = roleNames;
                authTokenDetailsDTO.expirationDate = tokenUtility.buildExpirationDate();

                // Create auth token
                jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
                if (jwt != null) {
                    response.addHeader("Authorization", jwt);
                    response.addHeader("Access-Control-Expose-Headers", "Authorization");
                }
                authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.status(HttpStatus.OK).body(user);
            } else {
                user = new User();
                user.setEmail(google.userOperations().getUserInfo().getId() + "@gmail.com");
                user.setFirstName(google.userOperations().getUserInfo().getFirstName());
                user.setLastName(google.userOperations().getUserInfo().getLastName());
                user.setPassword("1234");
                Role role = new Role();
                role.setRoleType(RoleType.ROLE_USER);
                user.setRole(role);
                user.setGoogleEmail(google.userOperations().getUserInfo().getEmail());
                user.setGoogleId(google.userOperations().getUserInfo().getId());
                user.setFacebookPic(google.userOperations().getUserInfo().getProfilePictureUrl());
                userService.save(user);

                roleNames.clear();
                roleNames.add(user.getRole().getRoleType().name());
                // Build the AuthTokenDetailsDTO
                authTokenDetailsDTO = new AuthTokenDetailsDTO();
                authTokenDetailsDTO.userId = "" + user.getId();
                authTokenDetailsDTO.email = user.getEmail();
                authTokenDetailsDTO.roleNames = roleNames;
                authTokenDetailsDTO.expirationDate = buildExpirationDate();

                // Create auth token
                jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
                if (jwt != null) {
                    response.addHeader("Authorization", jwt);
                    response.addHeader("Access-Control-Expose-Headers", "Authorization");
                }
                authentication = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return ResponseEntity.status(HttpStatus.OK).body(user);
            }


        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Token is not valid!");
        }
    }

    private Date buildExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date expirationDate = calendar.getTime();
        return expirationDate;
    }
}
