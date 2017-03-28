package search.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import search.model.*;
import search.model.dto.UserAndFacilities;
import search.model.dto.UserComment;
import search.service.persistence.*;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Date;
import java.util.List;

/**
 * Created by Hakan_Hyusein on 7/25/2016.
 */
@RestController
@RequestMapping("/facilities")
public class FacilityController {

    private FacilityService facilityService;
    private FacilityOwnerService facilityOwnerService;
    private RatingService ratingService;
    private UserService userService;
    private CommentService commentService;
    private ReservationService reservationService;
    private PictureService pictureService;
    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    public FacilityController(FacilityService facilityService, FacilityOwnerService facilityOwnerService, RatingService ratingService,
                              UserService userService, CommentService commentService, ReservationService reservationsService, PictureService pictureService) {
        this.facilityService = facilityService;
        this.facilityOwnerService = facilityOwnerService;
        this.ratingService = ratingService;
        this.userService = userService;
        this.commentService = commentService;
        this.reservationService = reservationsService;
        this.pictureService = pictureService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET)
    public List<Facility> allFacilities() {
        return this.facilityService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/favorite/{id}")
    public List<Facility> UserFavoriteFacilities(@PathVariable("id") int userId) {
        return this.facilityService.userFavoriteFacilities(userId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseBody
    public Facility createFacility(@RequestBody Facility facility) throws Exception {
        facility.setId(null);
        return (facilityService.save(facility));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Facility getFacilityById(@PathVariable("id") int id) {
        return facilityService.findById(id);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity updateFacilityComments(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @RequestBody UserComment userComment) {
        Facility facility = facilityService.findById(id);
        List<Comment> comments = facility.getComments();
        User user = userService.findById(userComment.getUser_id());
        Comment comment = new Comment();
        comment.setUser(user);
        comment.setText(userComment.getText());
        comment.setDate(new Date(System.currentTimeMillis()));
        comments.add(comment);
        facility.setComments(comments);

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.updateFacility(id, facility));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Facility> updateFacility(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Facility facility) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.updateFacility(id, facility));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{id}/comments")
    public ResponseEntity<List<Comment>> getFacilityComments(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.findByFacilityId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{id}/reservations")
    public ResponseEntity<List<Reservation>> getFacilityReservations(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByFacilityId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> delete(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.deleteFacility(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/weekendPeriodOfTime", method = RequestMethod.POST)
    public List<Facility> getInWeekendIntervalOfTime(@RequestBody WorkingHours weekendHours) {
        return facilityService.findByWeekendHours(weekendHours.getStartHour(), weekendHours.getEndHour());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/weekdayPeriodOfTime", method = RequestMethod.POST)
    public List<Facility> getInWorkdayPeriodOfTime(@RequestBody WorkingHours weekdayHours) {
        return facilityService.findByWeekdayHours(weekdayHours.getStartHour(), weekdayHours.getEndHour());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/getByAddress", method = RequestMethod.POST)
    public List<Facility> getByAddress(@RequestBody Address address) {
        return facilityService.findByAddress(address.getCity(), address.getNeighbourhood(), address.getStreet());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/getByOwner/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> getFacilitiesByOwner(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByFacilityOwnerId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/rating/{id}", method = RequestMethod.GET)
    public double getRating(@PathVariable Integer id) {
        return ratingService.findAvgByFacility(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/saveRating/{userId}/{facilityId}/{rating}", method = RequestMethod.POST)
    public ResponseEntity<List<Facility>> saveRating(@RequestHeader("Authorization") String token, @PathVariable Integer userId, @PathVariable Integer facilityId, @PathVariable Integer rating) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            Rating ratingForSave = new Rating();
            ratingForSave.setRating((double) rating);
            ratingForSave.setFacility(facilityService.findById(facilityId));
            ratingForSave.setUser(userService.findById(userId));
            ratingService.save(ratingForSave);
            return ResponseEntity.status(HttpStatus.OK).body(facilityService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "getByType/{type}", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> findByType(@PathVariable String type) {
        return ResponseEntity.status(HttpStatus.OK).body(facilityService.findByFacilityType(type));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "getByPrice/{price}", method = RequestMethod.GET)
    public List<Facility> findByPrice(@PathVariable Integer price) {
        return facilityService.findByPrice(price, price, price);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/getByRating", method = RequestMethod.GET)
    public ResponseEntity<List<Facility>> findByRating() {
        return ResponseEntity.status(HttpStatus.OK).body(facilityService.findAllOrderByRating());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/getFacilityTypes", method = RequestMethod.GET)
    public ResponseEntity<List<FacilityType>> findFacilityTypes() {
        return ResponseEntity.status(HttpStatus.OK).body(facilityService.facilityTypes());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/picture/{id}", method = RequestMethod.GET, produces = {MediaType.IMAGE_JPEG_VALUE})
    public byte[] returnRequestedPicture(@PathVariable("id") int id) {
        Picture image = pictureService.findOne(id);
        String path = image.getPath();

        FileInputStream fileInputStream = null;
        File picture = new File(path);
        byte[] bFile = new byte[(int) picture.length()];
        try {
            fileInputStream = new FileInputStream(picture);
            fileInputStream.read(bFile);
            fileInputStream.close();

            return bFile;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @RequestMapping(value = "/refresh/{id}", method = RequestMethod.GET)
    public UserAndFacilities refresh(@PathVariable("id") int id) {
        UserAndFacilities userAndFacilities = new UserAndFacilities();
        userAndFacilities.setUser(userService.findById(id));
        userAndFacilities.setFacilities(facilityService.findAll());
        return userAndFacilities;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/delete/comment/{id}", method = RequestMethod.POST)
    public Facility deleteComment(@PathVariable Integer id) {
        return commentService.deleteComment(id);
    }
}
