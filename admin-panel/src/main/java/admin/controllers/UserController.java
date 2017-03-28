package admin.controllers;

import admin.model.DeleteItems;
import admin.model.Reservation;
import admin.model.User;
import admin.service.persistance.UserService;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Created by Trayan_Muchev on 10/3/2016.
 */
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private UserService userService;
    private AuthTokenDetailsDTO detailsDTO;
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    public UserController(UserService userService, JsonWebTokenUtility tokenUtility) {
        this.tokenUtility = tokenUtility;
        this.userService = userService;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.findById(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<User>> all(@RequestHeader("Authorization") String token) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody User user) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserById(id, user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public void deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            this.userService.deleteUser(id);
            ResponseEntity.status(HttpStatus.OK);
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ResponseEntity<List<User>> deleteList(@RequestHeader("Authorization") String token, @RequestBody DeleteItems deleteUsers) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.deleteList(deleteUsers.getItemsId()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}/reservations", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getUserReservations(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserReservations(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "profilePic/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] returnRequestedPicture(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        User user = userService.findById(id);
        String path = user.getProfilePic();
        FileInputStream fileInputStream = null;
        File picture = new File(path);
        byte[] bFile = new byte[(int) picture.length()];
        if (picture != null) {
            try {
                fileInputStream = new FileInputStream(picture);
                fileInputStream.read(bFile);
                fileInputStream.close();

                return bFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
