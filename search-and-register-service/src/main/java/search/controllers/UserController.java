package search.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import search.exceptions.ExistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.exceptions.WrongPasswordException;
import search.model.Reservation;
import search.model.RoleType;
import search.model.User;
import search.model.dto.ChangePasswordDTO;
import search.model.dto.CreateUser;
import search.service.MailService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.lang.Boolean.TRUE;

/**
 * Created by Toncho_Petrov on 7/14/2016.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private FacilityOwnerService ownerService;

    @Autowired
    private MailService mailService;

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<User> getUserById(@RequestHeader(value = "Authorization") String token, HttpServletResponse response, @PathVariable("id") int id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO.email != null) {
            User user = userService.findById(id);
            detailsDTO.userId = user.getId().toString();
            detailsDTO.email = user.getEmail();
            detailsDTO.roleNames.add(user.getRole().getRoleType().name());
            return ResponseEntity.status(HttpStatus.OK).body(user);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createAndVerifyUser(@RequestBody CreateUser newUser, HttpServletRequest request) throws UnsupportedEncodingException, ExistingUserException {
        User user = new User(newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getPassword(), newUser.getPhone());
        user.setVerified(false);
        String jwt = constructingToken(user);
        user = userService.save(user);
        String hostname = request.getServerName();
        String addressForVerification = "http://" + hostname + ":3000/RegConfirmation?token=" + jwt + "&id=" + user.getId();
        sendMessage("Account Verification", "\nThank you for making a registration in our website.Click the link below to verify your account:\n" + addressForVerification, user.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.GET)
    ResponseEntity<List<User>> all(@RequestHeader("Authorization") String token) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<User> updateUser(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody User user) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.updateUserById(id, user));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    void deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            this.userService.deleteUser(id);
        } else {
            assert true : "delete user ";
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}/reservations", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getUserReservations(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);

        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(userService.getUserReservations(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "{id}/imageUpload", method = RequestMethod.POST)
    public User uploadProfilePic(@RequestHeader("Authorization") String token, @PathVariable("id") int userId,
                                 @RequestParam("image") MultipartFile file) throws FileUploadException, IOException {
        User user = userService.findById(userId);
        String path = ImageUploadController.IMAGES_FOLDER + user.getEmail();
        String fileName = new String();

        if (file != null && file.getSize() > 0) {
            fileName = ImageUploadController.saveFile(file, path);
        } else {
            throw new FileUploadException("Error uploading image. File is empty.");
        }

        user.setProfilePic(path + File.separator + fileName);
        user.setFacebookPic(null);
        userService.updateUserById(userId, user);
        return user;
    }

    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    @RequestMapping(value = "/profilePic/{id}", method = RequestMethod.GET, produces = {MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE})
    public byte[] returnRequestedPicture(@PathVariable("id") int id, HttpServletResponse response) {
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

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/changepass/{id}", method = RequestMethod.PUT)
    public ResponseEntity changeUserPassword(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id,
                                             @RequestBody ChangePasswordDTO user, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            try {
                userService.changePassword(id, user.getOldPassword(), user.getNewPassword(), user.getConfirmPassword());
            } catch (WrongPasswordException e) {
                throw new WrongPasswordException(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    @RequestMapping(value = "/forgottenPass", method = RequestMethod.PUT)
    public ResponseEntity forgottenPassword(HttpServletRequest request, @RequestBody Map<String, String> email) throws WrongEmailOrPassword {
        User user = userService.findByEmail(email.get("email"));
        if (user != null) {
            String hostname = request.getServerName();
            String jwt = constructingToken(user);
            String changePasswordUrl = "http://" + hostname + ":3000/ChangeForgottenPassword?token=" + jwt + "&id=" + user.getId();
            sendMessage("Forgotten Password", "\nHello,\n\t We received a request for changing your password in Epam Reservations." +
                    "\nIf you do not want to change your password, you can ignore this message. Click the link bellow to change your password.\n\t" + changePasswordUrl, user.getEmail());
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new WrongEmailOrPassword("User with this email does not exist");
        }
    }

    @RequestMapping(value = "/forgottenPassword/{id}", method = RequestMethod.PUT)
    public ResponseEntity changeForgottenPassword(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id,
                                                  @RequestBody ChangePasswordDTO changePasswordDTO) throws Exception {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            userService.updateUserForgottenPassword(id, changePasswordDTO);
            return new ResponseEntity(HttpStatus.OK);
        } else {
            throw new Exception("Current session has expired.\n" +
                    " The given time for changing your password has expired");
        }
    }

    @RequestMapping(value = "/registrationConfirmation/{id}", method = RequestMethod.POST)
    public User verifyUser(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, HttpServletRequest request, HttpServletResponse response) throws Exception {
        detailsDTO = tokenUtility.parseAndValidate(token);
        User user;
        if (detailsDTO != null) {
            user = userService.findById(id);
            user.setVerified(TRUE);
            user = userService.updateUserById(id, user);
            String jwt = constructingToken(user);
            if (jwt != null) {
                response.addHeader("Authorization", jwt);
                response.addHeader("Access-Control-Expose-Headers", "Authorization");
            } else {
                throw new Exception("A problem occurred. Please try again later.");
            }
            return user;
        } else {
            if (userService.findById(id) != null) {
                if (userService.findById(id).getVerified() == TRUE) {
                    user = userService.findById(id);
                    String jwt = constructingToken(user);
                    String hostname = request.getServerName();
                    String addressForVerification = "http://" + hostname + ":3000/RegConfirmation?token=" + jwt + "&id=" + user.getId();
                    sendMessage("Account Verification", "Thank you for making registration in our website. Click the link below to verify your account:" + addressForVerification, user.getEmail());
                    throw new Exception("Your verification time has expired. New verification email has been sent to your email address.");
                }
                throw new Exception("Current user already have been verified");
            } else {
                throw new Exception("The user you are trying to verify does not exist");
            }
        }
    }

    private String constructingToken(User user) {
        List<String> roleNames = new ArrayList<>();
        roleNames.add(RoleType.ROLE_USER.name());
        AuthTokenDetailsDTO authTokenDetailsDTO = new AuthTokenDetailsDTO();
        authTokenDetailsDTO.userId = String.valueOf(user.getId());
        authTokenDetailsDTO.email = user.getEmail();
        authTokenDetailsDTO.roleNames = roleNames;
        authTokenDetailsDTO.expirationDate = tokenUtility.buildVerificationExpirationDate(24);

        // Create auth token
        String jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
        return jwt;
    }

    void sendMessage(String subject, String text, String receiver) {
        mailService.sendEmail(subject, text, receiver);
    }
}
