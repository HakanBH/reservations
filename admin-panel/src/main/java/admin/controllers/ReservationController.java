package admin.controllers;

import admin.model.DeleteItems;
import admin.model.Reservation;
import admin.service.persistance.ReservationService;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.List;

/**
 * Created by Trayan_Muchev on 10/3/2016.
 */

@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {

    private AuthTokenDetailsDTO detailsDTO;
    private ReservationService reservationService;
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    public ReservationController(ReservationService reservationService, JsonWebTokenUtility tokenUtility) {
        this.reservationService = reservationService;
        this.tokenUtility = tokenUtility;
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity<List<Reservation>> delete(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.delete(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public List<Reservation> deleteList(@RequestBody DeleteItems deleteReservations) {
        return reservationService.deleteList(deleteReservations.getItemsId());
    }

    @RequestMapping(value = "/update/{id}", method = RequestMethod.PATCH)
    public Reservation updateReservation(@PathVariable Integer id, @RequestBody Reservation reservation) {
        return reservationService.update(id, reservation);
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getAllReservations(@RequestHeader("Authorization") String token) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<Reservation> getReservationById(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findById(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/facility/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationByFacilityId(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByFacilityId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationByUserId(@RequestHeader("Authorization") String token, @PathVariable Integer id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByUserId(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/after/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationByDateAfter(@RequestHeader("Authorization") String token, @PathVariable Date date) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByDateAfter(date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @RequestMapping(value = "/before/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationByDateBefore(@RequestHeader("Authorization") String token, @PathVariable Date date) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByDateBefore(date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationByDate(@RequestHeader("Authorization") String token, @PathVariable Date date) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByDate(date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{userId}/{facilityId}", method = RequestMethod.GET)
    public List<Reservation> getReservationsByUserAndFacility(@RequestHeader("Authorization") String token, @PathVariable Integer userId, @PathVariable Integer facilityId) {
        return reservationService.findByUserIdAndFacilityId(userId, facilityId);
    }

    @RequestMapping(value = "/findByFacility/{id}/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getReservationsFacilityAndDateAfter(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @PathVariable("date") Date date) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByFacilityIdAndDateAfter(id, date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

    }

    @RequestMapping(value = "/findByUser/{id}/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getUserReservationsDateAfter(@RequestHeader("Authorization") String token, @PathVariable("id") Integer id, @PathVariable("date") Date date) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByUserIdAndDateAfter(id, date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }
}
