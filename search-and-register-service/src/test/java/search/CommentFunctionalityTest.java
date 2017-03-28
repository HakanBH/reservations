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
import search.service.persistence.CommentService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;

import java.sql.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Trayan_Muchev on 8/16/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class CommentFunctionalityTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private FacilityOwnerService facilityOwnerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private FacilityTypeRepository facilityTypeRepository;

    private Comment comment;

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
            String facilityName = "Blq1";
            facility = facilityService.save(new Facility(address, user, facilityType, 200, facilityName));
            comment = commentService.save(new Comment("Comment", facility, user, Date.valueOf("2016-08-16")));
        } catch (ExistingUserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    public void Test1Save() {
        assertNotNull(comment);
    }

    @Test
    public void Test2FindByFacilityId() {
        List<Comment> comments = commentService.findByFacilityId(facility.getId());
        assertNotNull(comments);
    }

    @Test
    public void Test3FindByUserId() {
        List<Comment> comments = commentService.findByUserId(user.getId());
        assertNotNull(comments);
    }

    @Test
    public void Test4Update() {
        comment.setText("New Comment");
        comment = commentService.update(comment);
        assertEquals("New Comment", comment.getText());
    }

    @Test
    public void Test5FindById() {
        comment = commentService.findById(comment.getId());
        assertNotNull(comment);
    }

    @Test
    public void Test6Delete() {
        commentService.deleteComment(comment.getId());
        comment = commentService.findById(comment.getId());
        assertTrue(comment.isDeleted());
    }

}
