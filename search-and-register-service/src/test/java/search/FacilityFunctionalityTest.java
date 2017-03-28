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
import search.model.Address;
import search.model.Facility;
import search.model.FacilityOwner;
import search.model.FacilityType;
import search.repository.FacilityTypeRepository;
import search.service.persistence.AddressService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Trayan_Muchev on 8/17/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class FacilityFunctionalityTest {


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


    @Before
    public void setUp() {
        try {
            user = facilityOwnerService.save(new FacilityOwner("trayan95@abv.bg", "Trayan", "Muchev", "password", "0884345678"));
            address = new Address("Bulgaria", "Sofia", "JK St Grad", "Ul Tri Ushi");
            facilityType = facilityTypeRepository.findByType("Tennis");
            String facilityName = "Some name";
            facility = facilityService.save(new Facility(address, user, facilityType, 200, facilityName));
        } catch (ExistingUserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void Test1Save() {
        assertNotNull(facility);
    }

    @Test
    public void Test2FindById() {
        facility = facilityService.findById(facility.getId());
        assertNotNull(facility);
    }

    @Test
    public void Test3FindAll() {
        List<Facility> facilities = facilityService.findAll();
        assertNotNull(facilities);
    }

    @Test
    public void Test4Update() {
        facility.setPrice(150);
        facility = facilityService.updateFacility(facility.getId(), facility);
        assertEquals((Integer) 150, facility.getPrice());
    }

    @Test
    public void Test5FindByPrice() {
        List<Facility> facilities = facilityService.findByPrice(facility.getPrice(), facility.getPrice(), facility.getPrice());
        assertNotNull(facilities);
    }

    @Test
    public void Test6FindByFacilityOwnerId() {
        List<Facility> facilities = facilityService.findByFacilityOwnerId(user.getId());
        assertNotNull(facilities);
    }

    @Test
    public void Test7FindByFacilityType() {
        List<Facility> facilities = facilityService.findByFacilityType(facilityType.getType());
        assertNotNull(facilities);
    }

    @Test
    public void Test8FindAllOrderByRating() {
        List<Facility> facilities = facilityService.findAllOrderByRating();
        assertNotNull(facilities);
    }

    @Test
    public void Test9FindByAddress() {
        List<Facility> facilities = facilityService.findByAddress(address.getCity(), address.getNeighbourhood(), address.getStreet());
        assertNotNull(facilities);
    }

    @Test
    public void Test10FindFacilityTypes() {
        List<FacilityType> facilityTypes = facilityService.facilityTypes();
        assertNotNull(facilityTypes);
    }

    @Test
    public void Test11Delete() {
        facilityService.deleteFacility(facility.getId());
        facility = facilityService.findById(facility.getId());
        assertTrue(facility.isDeleted());
    }

}
