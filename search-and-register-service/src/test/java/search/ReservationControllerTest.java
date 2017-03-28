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
import search.controllers.ReservationController;
import search.model.*;
import search.service.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 11/22/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class ReservationControllerTest {

    private Long time = System.currentTimeMillis();
    private User testUser = new User("test12345@test.com", "Ant", "Antonov", "1234", "0887981231");
    private Address testAddres = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("owner12345@email.com", "Cola", "Dragicheva", "ciciCh", "0898123456");
    private FacilityType testFacilitytype = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddres, testFacilityOwner, testFacilitytype, 11, "Cola Tennis cort");
    private Reservation testReservation = new Reservation(testFacility, testUser, 11, 13, new Date(System.currentTimeMillis()), false);


    @Autowired
    private SearchAndRegisterCorsFilter searchAndRegisterCorsFilter;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private UserService userService;

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


        Role role = new Role();
        role.setRoleType(RoleType.ROLE_USER);
        testUser.setRole(role);
        userService.save(testUser);

        addressService.save(testAddres);
        testFacilitytype.setId(2);
        testFacilityOwner.setAddress(testAddres);

        facilityOwnerService.save(testFacilityOwner);

        testFacility.setFacilityOwner(testFacilityOwner);
        facilityService.save(testFacility);

        testReservation.setFacility(testFacility);
        testReservation.setUser(testUser);

        token = createTokenByRole(RoleType.ROLE_USER.toString());


        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
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
    public void addReservationAccessDeniedTest() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ANONYMOUS);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json = objectMapper.writeValueAsString(testReservation);
        MvcResult result = mockMvc.perform(post("/reservations")
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("addReservation"))
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    public void getReservationByIdTest() throws Exception {

        reservationService.save(testReservation);

        MvcResult result = mockMvc.perform(get("/reservations/{id}", testReservation.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationById"))
                .andExpect(status().isOk())
                .andReturn();

        Reservation reservation = objectMapper.readValue(result.getResponse().getContentAsString(), Reservation.class);

        TestCase.assertNotNull("Get reservationById == null", reservation);
    }


    @Test
    public void getReservationByFacilityIdTest() throws Exception {

        reservationService.save(testReservation);

        MvcResult result = mockMvc.perform(get("/reservations/facility/{id}", testReservation.getFacility().getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationByFacilityId"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationByFacilityId == null", reservations);
    }

    @Test
    public void getReservationByUserIdTest() throws Exception {

        reservationService.save(testReservation);

        MvcResult result = mockMvc.perform(get("/reservations/user/{id}", testUser.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationByUserId"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationByUserId == null", reservations);
    }

    @Test
    public void getReservationByDateAfterTest() throws Exception {

        reservationService.save(testReservation);
        Long time = System.currentTimeMillis() - (10 * 10000);
        Date date = new Date(time);
        MvcResult result = mockMvc.perform(get("/reservations/after/{date}", date)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationByDateAfter"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get getReservationByDateAfter == null", reservations);
    }

    @Test
    public void getReservationByDateBeforeTest() throws Exception {

        testReservation.setDate(new Date(System.currentTimeMillis() - 10000));
        reservationService.save(testReservation);
        Long time = System.currentTimeMillis();
        Date date = new Date(time);
        MvcResult result = mockMvc.perform(get("/reservations/before/{date}", date)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationByDateBefore"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get getReservationByDateBefore == null", reservations);
    }

    @Test
    public void getReservationByDateTest() throws Exception {

        reservationService.save(testReservation);
        Long time = System.currentTimeMillis();
        Date date = new Date(time);
        MvcResult result = mockMvc.perform(get("/reservations/date/{date}", date)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationByDate"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get getReservationByDate == null", reservations);
    }

    @Test
    public void getReservationsByUserAndFacilityTest() throws Exception {

        reservationService.save(testReservation);
        Long time = System.currentTimeMillis();
        Date date = new Date(time);
        MvcResult result = mockMvc.perform(get("/reservations/{userId}/{facilityId}", testUser.getId(), testFacility.getId())
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getReservationsByUserAndFacility"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get getReservationsByUserAndFacility == null", reservations);
    }


    private String createTokenByRole(String role) {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = testUser.getId().toString();
        detailsDTO.email = testUser.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add(role);
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }
}
