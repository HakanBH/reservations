package search;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
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
import search.configuration.security.SearchAndRegisterCorsFilter;
import search.controllers.FacilityController;
import search.model.*;
import search.model.dto.UserComment;
import search.repository.RoleRepository;
import search.service.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 11/25/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class FacilityControllerTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private SearchAndRegisterCorsFilter searchAndRegisterCorsFilter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private FacilityOwnerService ownerService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AddressService addressService;


    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;

    private MockMvc mockMvc;
    private String token;
    private Address testAddress = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("test1111@test.com", "Ant", "Antonov", "1234", "0887981231");
    private FacilityType testFacilityType = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddress, testFacilityOwner, testFacilityType, 11, "Cola Tennis cort");
    private Reservation testReservation = new Reservation(testFacility, testFacilityOwner, 15, 16, new Date(System.currentTimeMillis()), false);

    @Before
    public void setUp() {
        ownerService.save(testFacilityOwner);
        addressService.save(testAddress);
        testFacilityOwner.setAddress(testAddress);
        testFacility.setFacilityOwner(testFacilityOwner);
        testFacility.setAddress(testAddress);
        facilityService.save(testFacility);

        token = createTokenByRole(testFacilityOwner.getRole().getRoleType().toString());
        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_FACILITY_OWNER));

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .addFilters(searchAndRegisterCorsFilter)
                .build();
    }

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void allFacilitiesTest() throws Exception {
        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult result = mockMvc.perform(get("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("allFacilities"))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("Facility list is empty.", facilities);
    }

    @Test
    public void createFacilityTest() throws Exception {
        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json = objectMapper.writeValueAsString(testFacility);

        MvcResult result = mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("createFacility"))
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    public void createFacilityUnauthorizedTest() throws Exception {

        String json = objectMapper.writeValueAsString(testFacility);

        MvcResult result = mockMvc.perform(post("/facilities")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("createFacility"))
                .andExpect(status().isOk())
                .andReturn();

        Facility facility = objectMapper.readValue(result.getResponse().getContentAsString(), Facility.class);
        TestCase.assertNotNull("Facility ins't created.", facility);
    }

    @Test
    public void getFacilityByIdTest() throws Exception {

        facilityService.save(testFacility);

        MvcResult result = mockMvc.perform(get("/facilities/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("getFacilityById"))
                .andExpect(status().isOk())
                .andReturn();

        Facility facility = objectMapper.readValue(result.getResponse().getContentAsString(), Facility.class);
        TestCase.assertNotNull("getFacilityById retunt null", facility);

    }

    @Test
    public void updateFacilityCommentsTest() throws Exception {

        facilityService.save(testFacility);

        UserComment userComment = new UserComment();
        userComment.setUser_id(testFacilityOwner.getId());
        userComment.setText("Test comment.");
        String json = objectMapper.writeValueAsString(userComment);

        MvcResult result = mockMvc.perform(patch("/facilities/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("updateFacilityComments"))
                .andExpect(status().isOk())
                .andReturn();

        Facility commentedFacility = objectMapper.readValue(result.getResponse().getContentAsString(), Facility.class);
        TestCase.assertNotNull("No Comments", commentedFacility.getComments().get(0).getText());
    }

    @Test
    public void commentFacilityUnauthorizedTest() throws Exception {
        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_ANONYMOUS));

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserComment userComment = new UserComment();
        userComment.setUser_id(testFacilityOwner.getId());
        userComment.setText("Test comment.");

        String json = objectMapper.writeValueAsString(userComment);

        MvcResult result = mockMvc.perform(patch("/facilities/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("updateFacilityComments"))
                .andExpect(status().isForbidden())
                .andReturn();
    }


    @Test
    public void updateFacilityTest() throws Exception {
        Facility facility = new Facility();
        facility.setAddress(testFacility.getAddress());
        facility.setFacilityOwner(testFacility.getFacilityOwner());
        facility.setName("Updated name");
        String json = objectMapper.writeValueAsString(facility);

        MvcResult result = mockMvc.perform(patch("/facilities/update/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("updateFacility"))
                .andExpect(status().isOk())
                .andReturn();

        Facility updatedFacility = objectMapper.readValue(result.getResponse().getContentAsString(), Facility.class);
        TestCase.assertNotSame("Facilities is the same update fail.", updatedFacility.getName(), testFacility.getName());
    }

    @Test
    public void getFacilityCommentsTest() throws Exception {

        String text = "Tic Tac Toe";
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUser(testFacilityOwner);
        List<Comment> comments = new ArrayList<>();
        comments.add(comment);
        testFacility.setComments(comments);
        facilityService.updateFacility(testFacility.getId(), testFacility);

        MvcResult result = mockMvc.perform(get("/facilities/{id}/comments", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("getFacilityComments"))
                .andExpect(status().isOk())
                .andReturn();

        List<Comment> list = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Comment[].class));
        TestCase.assertEquals("Facility doesn't have any comments.", text, list.iterator().next().getText());
    }

    @Test
    public void getFacilityReservationsTest() throws Exception {

        testReservation.setUser(testFacilityOwner);
        testReservation.setFacility(testFacility);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(testReservation);
        testFacility.setReservations(reservations);
        facilityService.updateFacility(testFacility.getId(), testFacility);

        reservationService.save(testReservation);

        MvcResult result = mockMvc.perform(get("/facilities/{id}/reservations", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("getFacilityReservations"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservationList = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        User reservationUser = reservationList.iterator().next().getUser();
        boolean isPass = false;
        if (reservationUser.getId().equals(testFacilityOwner.getId())
                && testReservation.getToHour().equals(reservationList.iterator().next().getToHour())
                && testReservation.getFromHour().equals(reservationList.iterator().next().getFromHour())) {
            isPass = true;
        }
        TestCase.assertTrue("Test user don't have any reservation.", isPass);
    }

    @Test
    public void deleteFacilityTest() throws Exception {

        facilityService.save(testFacility);

        MvcResult result = mockMvc.perform(post("/facilities/delete/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> deletedFacility = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("Facility is doesn't deleted.", deletedFacility.contains(testFacility));
    }

    @Test
    public void deleteFacilityUnauthorizedTest() throws Exception {
        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult result = mockMvc.perform(post("/facilities/delete/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void getByAddressTest() throws Exception {

        facilityService.save(testFacility);

        MvcResult result = mockMvc.perform(get("/facilities/getByType/{type}", testFacility.getFacilityType().getType())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("findByType"))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("No facilities whit type: " + testFacility.getFacilityType().getType(),
                facilities.stream().filter(facility -> facility.getFacilityType().getType().equals(testFacility.getFacilityType().getType()))
                        .filter(facility -> facility.getId().equals(testFacility.getId()))
                        .findFirst().get());
    }

    @Test
    public void findFacilityByTypeTest() throws Exception {

        facilityService.save(testFacility);

        String json = objectMapper.writeValueAsString(testAddress);

        MvcResult result = mockMvc.perform(post("/facilities/getByAddress")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("getByAddress"))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("No facility on this address.", facilities.stream()
                .filter(facility ->
                        facility.getId().equals(testFacility.getId()))
                .filter(facility ->
                        facility.getAddress().getNeighbourhood().equals(testFacility.getAddress().getNeighbourhood())));
    }

    @Test
    public void findByRatingTest() throws Exception {

        testFacility.setRating(3.5);
        facilityService.save(testFacility);

        MvcResult result = mockMvc.perform(get("/facilities/getByRating")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityController.class))
                .andExpect(handler().methodName("findByRating"))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));


        TestCase.assertNotNull("No facilities whit reiting: " + testFacility.getRating(), facilities.stream()
                .filter(facility -> facility.getId().equals(testFacility.getId()))
                .filter(facility -> facility.getRating() == 3.5).findFirst().get());
    }

    private String createTokenByRole(String role) {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "33";
        detailsDTO.email = testFacilityOwner.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add(role);
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }
}
