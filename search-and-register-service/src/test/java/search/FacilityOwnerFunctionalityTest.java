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
import search.model.FacilityOwner;
import search.service.persistence.FacilityOwnerService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Trayan_Muchev on 8/17/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class FacilityOwnerFunctionalityTest {

    @Autowired
    private FacilityOwnerService facilityOwnerService;


    private FacilityOwner facilityOwner;

    @Before
    public void setUp() {
        try {
            facilityOwner = facilityOwnerService.save(new FacilityOwner("trayan95@abv.bg", "Trayan", "Muchev", "password", "0884300666"));
        } catch (ExistingUserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test1Save() {
        assertNotNull(facilityOwner);
    }

    @Test
    public void Test2FindOne() {
        facilityOwner = facilityOwnerService.findById(facilityOwner.getId());
        assertNotNull(facilityOwner);
    }

    @Test
    public void Test3FindAll() {
        List<FacilityOwner> facilityOwners = facilityOwnerService.findAll();
        assertNotNull(facilityOwners);
    }

    @Test
    public void Test4Update() {
        facilityOwner.setFirstName("Hakan");
        facilityOwner = facilityOwnerService.updateOwner(facilityOwner);
        assertEquals("Hakan", facilityOwner.getFirstName());
    }

    @Test
    public void Test5FindByEmail() {
        facilityOwner = facilityOwnerService.findByEmail(facilityOwner.getEmail());
        assertNotNull(facilityOwner);
    }

    @Test
    public void Test6FindAllOrderByRating() {
        List<FacilityOwner> facilityOwners = facilityOwnerService.findAllByOrderByRatingDesc();
        assertNotNull(facilityOwners);
    }

    @Test
    public void Test7FindByEmailAndPassword() {
        FacilityOwner owner = facilityOwnerService.findByEmailAndPassword(facilityOwner.getEmail(), "password");
        assertNotNull(owner);
    }

    @Test
    public void Test8Delete() {
        facilityOwnerService.deleteUser(facilityOwner.getId());
        facilityOwner = facilityOwnerService.findById(facilityOwner.getId());
        assertTrue(facilityOwner.isDeleted());
    }
}
