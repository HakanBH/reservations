package reservations.controllers;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import reservations.model.Facility;
import reservations.model.Reservation;
import reservations.model.User;

import java.util.List;

@Component
@FeignClient("search-and-register-service")
public interface SearchAndRegisterClient {

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.GET, value = "/users")
    List<User> all(@RequestHeader("Authorization") String token);

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(method = RequestMethod.POST, value = "/reservations")
    ResponseEntity addReservation(@RequestHeader("Authorization") String token, @RequestBody Reservation reservation);

    @RequestMapping(method = RequestMethod.GET, value = "/reservations")
    List<Reservation> getAllReservations();

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/facilities/{id}", method = RequestMethod.GET)
    public Facility getFacilityById(@RequestHeader("Authorization") String token, @PathVariable("id") int id);

    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN','ROLE_FACILITY_OWNER')")
    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUsersById(@RequestHeader("Authorization") String token, @PathVariable("id") int id);
}
