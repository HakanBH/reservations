package search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import search.exceptions.ExistingUserException;
import search.model.*;
import search.repository.FacilityTypeRepository;
import search.service.persistence.AddressService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;
import search.service.persistence.ReservationService;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Trayan_Muchev on 8/17/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class ReservationFunctionalityTest {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityOwnerService facilityOwnerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private FacilityTypeRepository facilityTypeRepository;

    private FacilityOwner user;

    private Facility facility;

    private Address address;

    private FacilityType facilityType;

    private Reservation reservation;


    @Before
    public void setUp() {
        try {
            user = facilityOwnerService.save(new FacilityOwner("trayan95@abv.bg", "Trayan", "Muchev", "password", "0884345678"));
            address = new Address("Bulgaria", "Sofia", "JK St Grad", "Ul Tri Ushi");
            facilityType = facilityTypeRepository.findByType("Tennis");
            String facilityName = "Some name";
            facility = facilityService.save(new Facility(address, user, facilityType, 200, facilityName));
            reservation = reservationService.save(new Reservation(facility, user, 11, 12, Date.valueOf("2016-08-17"), false));
        } catch (ExistingUserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void Test1Save() {
        assertNotNull(reservation);
    }

    @Test
    public void Test2FindById() {
        reservation = reservationService.findById(reservation.getId());
        assertNotNull(reservation);
    }

    @Test
    public void Test3FindAll() {
        List<Reservation> reservations = reservationService.findAll();
        assertNotNull(reservations);
    }

    @Test
    public void Test4FindByFacilityId() {
        List<Reservation> reservations = reservationService.findByFacilityId(facility.getId());
        assertNotNull(reservations);
    }

    @Test
    public void Test5FindByUserId() {
        List<Reservation> reservations = reservationService.findByUserId(user.getId());
        assertNotNull(reservations);
    }

    @Test
    public void Test6FindByDateAfter() {
        List<Reservation> reservations = reservationService.findByDateAfter(Date.valueOf("2016-08-16"));
        assertNotNull(reservations);
    }

    @Test
    public void Test7FindByDateBefore() {
        List<Reservation> reservations = reservationService.findByDateBefore(Date.valueOf("2016-08-18"));
        assertNotNull(reservations);
    }

    @Test
    public void Test8FindByDate() {
        List<Reservation> reservations = reservationService.findByDate(Date.valueOf("2016-08-17"));
        assertNotNull(reservations);
    }

    @Test
    public void Test9FindByUserAndFacilityId() {
        List<Reservation> reservations = reservationService.findByUserIdAndFacilityId(user.getId(), facility.getId());
        assertNotNull(reservations);
    }

    @Test
    public void Test10Delete() {
        reservationService.delete(reservation.getId());
        reservation = reservationService.findById(reservation.getId());
        assertNull(reservation);
    }
}
