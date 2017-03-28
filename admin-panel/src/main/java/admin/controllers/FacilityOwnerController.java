package admin.controllers;

import admin.model.DeleteItems;
import admin.model.FacilityOwner;
import admin.service.persistance.FacilityOwnerService;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Trayan_Muchev on 10/3/2016.
 */
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping("/owners")
public class FacilityOwnerController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    FacilityOwnerService facilityOwnerService;


    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<FacilityOwner>> getAllOwners(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<FacilityOwner> getOwnerById(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.findById(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<FacilityOwner> updateUser(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @RequestBody FacilityOwner owner) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            owner.setId(id);
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.updateOwner(owner));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<Boolean> delete(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            facilityOwnerService.deleteUser(id);
            return ResponseEntity.status(HttpStatus.OK).body(true);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public List<FacilityOwner> deleteList(@RequestBody DeleteItems deleteOwners) {
        return facilityOwnerService.deleteList(deleteOwners.getItemsId());
    }

    @RequestMapping(value = "/getByRating", method = RequestMethod.GET)
    public ResponseEntity<List<FacilityOwner>> findByRating(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityOwnerService.findAllByOrderByRatingDesc());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
