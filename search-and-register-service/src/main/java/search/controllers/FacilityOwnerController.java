package search.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import search.exceptions.ExistingUserException;
import search.model.Facility;
import search.model.FacilityOwner;
import search.model.RoleType;
import search.model.User;
import search.model.dto.CreateUser;
import search.service.MailService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;
import search.service.persistence.UserService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@RestController
@RequestMapping("/owners")
public class FacilityOwnerController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    FacilityOwnerService facilityOwnerService;

    @Autowired
    FacilityService facilityService;

    @Autowired
    private UserService userService;

    @Autowired
    MailService mailService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<FacilityOwner> getAllOwners() {
        return facilityOwnerService.findAll();
    }

    @PreAuthorize("hasAuthority('ROLE_ANONYMOUS')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createAndVerifyOwner(@RequestBody CreateUser newUser, HttpServletRequest request) throws ExistingUserException, IllegalArgumentException {
        if (newUser.getPassword().equals("") || newUser.getPassword() == null) {
            throw new IllegalArgumentException("Password field is empty!!!");
        }
        FacilityOwner facilityOwner = new FacilityOwner(newUser.getEmail(), newUser.getFirstName(), newUser.getLastName(), newUser.getPassword(), newUser.getPhone());
        String jwt = constructingToken(facilityOwner);
        facilityOwner = facilityOwnerService.save(facilityOwner);
        String hostname = request.getServerName();
        String addressForVerification = "http://" + hostname + ":3000/RegConfirmation?token=" + jwt + "&id=" + facilityOwner.getId();
        mailService.sendEmail("Account Verification", "Thank you for making a registration in our website. Click the link below to verify your account:" + addressForVerification, facilityOwner.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/{id}/facilities", method = RequestMethod.POST)
    public ResponseEntity<Facility> createFacility(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @RequestBody Facility facility) throws Exception {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            FacilityOwner owner = facilityOwnerService.findById(id);
            facility.setFacilityOwner(owner);
            return ResponseEntity.status(HttpStatus.CREATED).body(facilityService.save(facility));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public FacilityOwner getOwnerById(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {
        return facilityOwnerService.findById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<FacilityOwner> updateUser(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @RequestBody FacilityOwner owner) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null && detailsDTO.userId.equals(id + "")) {
            owner.setId(id);
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.updateOwner(owner));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Boolean> delete(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            facilityOwnerService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(false);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/getByRating", method = RequestMethod.GET)
    public ResponseEntity<List<FacilityOwner>> findByRating(@RequestHeader("Authorization") String token) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.findAllByOrderByRatingDesc());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    private Date buildExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date expirationDate = calendar.getTime();
        return expirationDate;
    }

    String constructingToken(User user) {
        List roleNames = new ArrayList<String>();
        roleNames.add(RoleType.ROLE_USER);
        AuthTokenDetailsDTO authTokenDetailsDTO = new AuthTokenDetailsDTO();
        authTokenDetailsDTO.userId = user.getId() + "";
        authTokenDetailsDTO.email = user.getEmail();
        authTokenDetailsDTO.roleNames = roleNames;
        authTokenDetailsDTO.expirationDate = tokenUtility.buildVerificationExpirationDate(24);

        // Create auth token
        String jwt = tokenUtility.createJsonWebToken(authTokenDetailsDTO);
        return jwt;
    }
}
