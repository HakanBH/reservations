package reservations;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
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
import reservations.controllers.ReservationController;
import reservations.model.*;
import reservations.service.ReservationService;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 11/29/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ReservationsApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("reservations.controllers")
@IntegrationTest({"server.port=0", "management.port=0", "spring.profiles.active=test"})
public class ReservationControllerTest {


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;

    private MockMvc mockMvc;
    private String token;
    private Address testAddres = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testUser = new FacilityOwner("test1111@test.com", "Ant", "Antonov", "1234", "0887981231");
    private FacilityOwner testFacilityOwner = testUser;
    private FacilityType testFacilitytype = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddres, testFacilityOwner, testFacilitytype, 11, "Cola Tennis cort");
    private Reservation testReservation = new Reservation(testFacility, testUser, 15, 16, new Date(System.currentTimeMillis()), false);


    @Before
    public void setUp() {

//        testUser.setId(33);
//        testAddres.setId(2);
//        testFacilityOwner.setId(33);
        testFacilitytype.setId(2);
//
//        testFacilityOwner.setId(testUser.getId());
//        testFacilityOwner.setAddress(testAddres);
//        testFacility.setFacilityOwner(testFacilityOwner);
//        testFacility.setId(33);
//        testReservation.setId(214);

//        Role role = new Role();
//        role.setRoleType(RoleType.ROLE_USER);
//        testUser.setRole(role);
//        userService.save(testUser);
//
//        addressService.save(testAddres);
//        testFacilitytype.setId(2);
//        testFacilityOwner.setAddress(testAddres);
//
//        facilityOwnerService.save(testFacilityOwner);
//
//        testFacility.setFacilityOwner(testFacilityOwner);
//        facilityService.save(testFacility);
//
//        testReservation.setFacility(testFacility);
//        testReservation.setUser(testUser);

        token = createTokenByRole(RoleType.ROLE_USER.toString());

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_USER);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();
    }

    @After
    public void clearContext() {
        SecurityContextHolder.clearContext();
    }


    @Test
    public void getUsersTest() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String adminToken = createTokenByRole(RoleType.ROLE_ADMIN.toString());

        MvcResult result = mockMvc.perform(get("/reservations/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", adminToken)
                .content(adminToken))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getUsers"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> users = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), User[].class));
        TestCase.assertNotNull("Not found any users.", users);
    }

    @Test
    public void getAllReservationsTest() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
//        testReservation.setFacility(testFacility);
//        reservationService.save(testReservation);

        MvcResult result = mockMvc.perform(get("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getAllReservations"))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("No reservations.", reservations);
    }

//    @Test
//    public void deleteReservationTest() throws Exception {
//
//        testReservation.setId(214);
//        reservationService.save(testReservation);
//
//        MvcResult result =  mockMvc.perform(post("/reservations/delete/{id}",testReservation.getId())
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .header("Authorization",token))
//                .andExpect(handler().handlerType(ReservationController.class))
//                .andExpect(handler().methodName("delete"))
//                .andExpect(status().isOk())
//                .andReturn();
//        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(),Reservation[].class));
//        System.out.println(reservations);
//        TestCase.assertNull("The reservation is doesn't deleted.",reservations.stream().filter(reservation -> reservation.getId().equals(testReservation.getId())).findFirst().get());
//    }

    private String createTokenByRole(String role) {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "11111";
        detailsDTO.email = testUser.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add(role);
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }

}
