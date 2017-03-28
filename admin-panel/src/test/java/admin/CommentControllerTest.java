package admin;

import admin.model.*;
import admin.service.persistance.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Trayan_Muchev on 1/13/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AdminApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("admin.controllers")
public class CommentControllerTest {

    private Long time = System.currentTimeMillis();
    private Address testAddress = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("owner12345@email.com", "Cola", "Dragicheva", "ciciCh", "0898123456");
    private FacilityType testFacilitytype = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddress, testFacilityOwner, testFacilitytype, 11, "Cola Tennis cort");
    private Comment testComment = new Comment("Comment", testFacility, testFacilityOwner, new Date(System.currentTimeMillis()));


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FacilityOwnerService facilityOwnerService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;

    private MockMvc mockMvc;

    private String token;

    @Before
    public void setUp() throws Exception {


        List<String> roleNames = new ArrayList<>();
        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testFacilityOwner.setRole(role);
        roleNames.add(testFacilityOwner.getRole().getRoleType().name());


        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        testFacilityOwner = facilityOwnerService.save(testFacilityOwner);
        token = createTokenByRole(RoleType.ROLE_ADMIN.toString());
        testFacilitytype.setId(2);
        testFacility.setFacilityOwner(testFacilityOwner);
        testFacility.setAddress(testAddress);
        testFacility.setFacilityType(testFacilitytype);
        testFacility.setPictures(new ArrayList<>());
        testFacility = facilityService.save(testFacility);
        testComment.setFacility(testFacility);
        testComment.setUser(testFacilityOwner);
        testComment = commentService.save(testComment);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());


        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();

    }


    @Test
    public void getAllCommentsTest() throws Exception {

        MvcResult result = mockMvc.perform(get("/comments")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        List<Comment> comments = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Comment[].class));
        TestCase.assertNotNull("Get reservationById == null", comments);
    }

    @Test
    public void updateComment() throws Exception {
        final String oldText = testComment.getText();
        testComment.setText("Text");

        String json = objectMapper.writeValueAsString(testComment);

        MvcResult result = mockMvc.perform(patch("/comments/{id}", testComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        Comment updatedComment = commentService.findById(testComment.getId());
        TestCase.assertNotSame(oldText, updatedComment.getDate());
    }

    @Test
    public void getById() throws Exception {

        MvcResult result = mockMvc.perform(get("/comments/{id}", testComment.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        Comment comment = objectMapper.readValue(result.getResponse().getContentAsString(), Comment.class);
        TestCase.assertNotNull("The user == null.", comment);
    }

    @Test
    public void deleteCommentTest() throws Exception {

        MvcResult result = mockMvc.perform(post("/comments/delete/{id}", testComment.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        Comment comment = commentService.findById(testComment.getId());
        TestCase.assertTrue("The comment isDeleted.", comment.isDeleted());

    }

    @Test
    public void deleteListTest() throws Exception {

        DeleteItems deleteItems = new DeleteItems();
        List<Integer> deletedId = new ArrayList<>();
        deletedId.add(testComment.getId());
        deleteItems.setItemsId(deletedId);

        String json = objectMapper.writeValueAsString(deleteItems);

        MvcResult result = mockMvc.perform(post("/comments/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }


    private String createTokenByRole(String role) {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = testFacilityOwner.getId().toString();
        detailsDTO.email = testFacilityOwner.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add(role);
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }
}
