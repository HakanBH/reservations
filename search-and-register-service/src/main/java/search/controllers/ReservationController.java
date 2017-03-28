package search.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import search.model.Reservation;
import search.service.persistence.ReservationService;

import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Reservation> addReservation(@RequestHeader("Authorization") String token, @RequestBody Reservation reservation) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.save(reservation));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Reservation> getReservationById(@RequestHeader("Authorization") String token, @PathVariable Integer id, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findById(id));
        } else {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/facility/{id}", method = RequestMethod.GET)
    public ResponseEntity getReservationByFacilityId(@RequestHeader("Authorization") String token, @PathVariable Integer id, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByFacilityId(id));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity getReservationByUserId(@RequestHeader("Authorization") String token, @PathVariable Integer id, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByUserId(id));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/after/{date}", method = RequestMethod.GET)
    public ResponseEntity getReservationByDateAfter(@RequestHeader("Authorization") String token, @PathVariable Date date, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByDateAfter(date));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/before/{date}", method = RequestMethod.GET)
    public ResponseEntity getReservationByDateBefore(@RequestHeader("Authorization") String token, @PathVariable Date date, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByDateBefore(date));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    public ResponseEntity getReservationByDate(@RequestHeader("Authorization") String token, @PathVariable Date date, HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByDate(date));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/{userId}/{facilityId}", method = RequestMethod.GET)
    public ResponseEntity getReservationsByUserAndFacility(@RequestHeader("Authorization") String token,
                                                           @PathVariable Integer userId,
                                                           @PathVariable Integer facilityId,
                                                           HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            response.addHeader("Authorization", token);
            response.addHeader("Access-Control-Expose-Headers", "Authorization");
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(reservationService.findByUserIdAndFacilityId(userId, facilityId));
        } else {
            return (ResponseEntity) ResponseEntity
                    .status(HttpStatus.FORBIDDEN);
        }
    }
}
