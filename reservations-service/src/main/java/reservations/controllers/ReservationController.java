package reservations.controllers;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reservations.configuration.broadcast.Producer;
import reservations.model.Facility;
import reservations.model.PendingAndRejectedReservation;
import reservations.model.Reservation;
import reservations.model.User;
import reservations.service.ReservationService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Trayan_Muchev on 9/8/2016.
 */
@RestController
@RequestMapping(value = "/reservations")
public class ReservationController {

    private AuthTokenDetailsDTO detailsDTO;
    private Producer producer = new Producer();

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    SearchAndRegisterClient searchAndRegisterClient;

    private ReservationService reservationService;

    @Autowired
    public ReservationController(ReservationService reservationService) throws IOException, TimeoutException {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    public ResponseEntity<List<User>> getUsers(@RequestHeader("Authorization") String token) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(searchAndRegisterClient.all(token));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    public ResponseEntity delete(@RequestHeader("Authorization") String token, @PathVariable Integer id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {

            try {
                producer.sendDeletedReservation(reservationService.findById(id));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.OK).body(reservationService.delete(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity addReservation(@RequestHeader("Authorization") String token, @RequestBody Reservation reservation) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {

            synchronized (reservation) {
                reservation = reservationService.save(reservation);
                User user = searchAndRegisterClient.getUsersById(token, reservation.getUser().getId());
                Facility facility = searchAndRegisterClient.getFacilityById(token, reservation.getFacility().getId());
                reservation.setUser(user);
                reservation.setFacility(facility);
            }

            try {
                producer.sendNewReservation(reservation);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

            return ResponseEntity.status(HttpStatus.OK).body(reservation);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET)
    public List<Reservation> getAllReservations() {
        return reservationService.findAll();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Reservation getReservationById(@PathVariable Integer id) {
        return reservationService.findById(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/facility/{id}", method = RequestMethod.GET)
    public List<Reservation> getReservationByFacilityId(@PathVariable Integer id) {
        return reservationService.findByFacilityId(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public List<Reservation> getReservationByUserId(@PathVariable Integer id) {
        return reservationService.findByUserId(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/after/{date}", method = RequestMethod.GET)
    public List<Reservation> getReservationByDateAfter(@PathVariable Date date) {
        return reservationService.findByDateAfter(date);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/before/{date}", method = RequestMethod.GET)
    public List<Reservation> getReservationByDateBefore(@PathVariable Date date) {
        return reservationService.findByDateBefore(date);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    public List<Reservation> getReservationByDate(@PathVariable Date date) {
        return reservationService.findByDate(date);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/{userId}/{facilityId}", method = RequestMethod.GET)
    public List<Reservation> getReservationsByUserAndFacility(@PathVariable Integer userId, @PathVariable Integer facilityId) {
        return reservationService.findByUserIdAndFacilityId(userId, facilityId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/findByFacility/{id}/{date}", method = RequestMethod.GET)
    public List<Reservation> getReservationsFacilityAndDateAfter(@PathVariable("id") Integer id, @PathVariable("date") Date date) {
        return reservationService.findByFacilityIdAndDateAfter(id, date);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/findByUser/{id}/{date}", method = RequestMethod.GET)
    public ResponseEntity<List<Reservation>> getUserReservationsDateAfter(@RequestHeader("Authorization") String token,
                                                                          @PathVariable("id") Integer id,
                                                                          @PathVariable("date") Date date,
                                                                          HttpServletResponse response) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(reservationService.findByUserIdAndDateAfter(id, date));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    //Pending and Rejected Reservations

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(value = "/approve/{id}", method = RequestMethod.PATCH)
    public Reservation approveReservation(@PathVariable Integer id) {
        return reservationService.approveReservation(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/pending/rejected")
    public List<PendingAndRejectedReservation> getAllPendingAndRejectedReservations() {
        return reservationService.findAllPendingAndRejectedReservations();
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/pending/{userId}")
    public List<PendingAndRejectedReservation> getAllPendingReservationsByUser(@PathVariable Integer userId) {
        return reservationService.findPendingReservationsByUserId(userId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/pending/{userId}/{facilityId}")
    public List<PendingAndRejectedReservation> getAllPendingReservationsByUserAndFacility(@PathVariable Integer userId, @PathVariable Integer facilityId) {
        return reservationService.findPendingReservationsByUserIdAndFacilityId(userId, facilityId);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ANONYMOUS','ROLE_ADMIN','ROLE_FACILITY_OWNER','ROLE_USER')")
    @RequestMapping(method = RequestMethod.GET, value = "/pending/owner/{ownerId}")
    public List<PendingAndRejectedReservation> getAllPendingReservationsByOwner(@PathVariable Integer ownerId) {
        return reservationService.findPendingReservationsByOwnerId(ownerId);
    }


    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.POST, value = "/pending")
    public ResponseEntity addPendingReservation(@RequestHeader("Authorization") String token, @RequestBody Reservation reservation) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            PendingAndRejectedReservation pendingAndRejectedReservation = reservationService.savePendingReservation(reservation);
            User user = searchAndRegisterClient.getUsersById(token, reservation.getUser().getId());
            Facility facility = searchAndRegisterClient.getFacilityById(token, reservation.getFacility().getId());
            pendingAndRejectedReservation.setUser(user);
            pendingAndRejectedReservation.setFacility(facility);
            return ResponseEntity.status(HttpStatus.OK).body(pendingAndRejectedReservation);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }


}
