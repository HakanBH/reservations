package admin.controllers;

import admin.feignClient.SearchAndRegisterClient;
import admin.model.*;
import admin.service.persistance.*;
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
@RequestMapping("/facilities")
public class FacilityController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    SearchAndRegisterClient searchAndRegisterClient;

    private FacilityService facilityService;
    private RatingService ratingService;
    private CommentService commentService;
    private ReservationService reservationService;
    private PictureService pictureService;

    @Autowired
    public FacilityController(FacilityService facilityService, RatingService ratingService, CommentService commentService,
                              ReservationService reservationsService, PictureService pictureService) {
        this.facilityService = facilityService;
        this.ratingService = ratingService;
        this.commentService = commentService;
        this.reservationService = reservationsService;
        this.pictureService = pictureService;
    }


    @RequestMapping(method = RequestMethod.GET, value = "/fullFacilitiesInfo")
    public ResponseEntity<List<FacilityFeign>> getUsers(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(searchAndRegisterClient.all(token));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> allFacilities(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            List<Facility> facilities = this.facilityService.findAll();
            System.out.println(facilities);
            return ResponseEntity.status(HttpStatus.OK).body(facilities);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Facility> getFacilityById(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findById(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Facility> updateFacility(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Facility facility) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.updateFacilityById(id, facility));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}/comments")
    public ResponseEntity<List<Comment>> getFacilityComments(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.findByFacilityId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}/reservations")
    public ResponseEntity<List<Reservation>> getFacilityReservations(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByFacilityId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> delete(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.deleteFacility(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public List<Facility> deleteList(@RequestBody DeleteItems deleteFacilities) {
        return facilityService.deleteList(deleteFacilities.getItemsId());
    }

    @RequestMapping(value = "/weekendPeriodOfTime", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> getInWeekendIntervalOfTime(@RequestHeader("Authorization") String token, @RequestBody WorkingHours weekendHours) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByWeekendHours(weekendHours.getStartHour(), weekendHours.getEndHour()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @RequestMapping(value = "/weekdayPeriodOfTime", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> getInWorkdayPeriodOfTime(@RequestHeader("Authorization") String token, @RequestBody WorkingHours weekdayHours) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByWeekdayHours(weekdayHours.getStartHour(), weekdayHours.getEndHour()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/getByAddress", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> getByAddress(@RequestHeader("Authorization") String token, @RequestBody Address address) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByAddress(address.getCity(), address.getNeighbourhood(), address.getStreet()));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/getByOwner/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> getFacilitiesByOwner(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByFacilityOwnerId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/rating/{id}", method = RequestMethod.GET)
    public ResponseEntity getRating(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(ratingService.findAvgByFacility(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "getByType/{type}", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> findByType(@RequestHeader("Authorization") String token, @PathVariable String type) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByFacilityType(type));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "getByPrice/{price}", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> findByPrice(@RequestHeader("Authorization") String token, @PathVariable Integer price) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByPrice(price, price, price));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/getByRating", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> findByRating(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findAllOrderByRating());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/getFacilityTypes", method = RequestMethod.GET)
    public ResponseEntity<List<FacilityType>> findFacilityTypes(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.facilityTypes());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @ResponseBody
    @RequestMapping(value = "/picture/{id}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    public byte[] returnRequestedPicture(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        Picture image = pictureService.findOne(id);
        String path = image.getPath();

        System.out.println(path);

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
