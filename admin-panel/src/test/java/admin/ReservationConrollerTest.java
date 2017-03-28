package admin;

import admin.controllers.ReservationController;
import admin.model.*;
import admin.service.persistance.*;
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
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.scheduling.annotation.EnableScheduling;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Trayan_Muchev on 1/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AdminApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("admin.controllers")
@EnableScheduling
public class ReservationConrollerTest {

    private Long time = System.currentTimeMillis();
    private FacilityOwner testUser = new FacilityOwner("test12345@test.com", "Ant", "Antonov", "1234", "0887981231");
    private Address testAddres = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("owner12345@email.com", "Cola", "Dragicheva", "ciciCh", "0898123456");
    private FacilityType testFacilitytype = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddres, testFacilityOwner, testFacilitytype, 11, "Cola Tennis cort");
    private Reservation testReservation = new Reservation(testFacility, testUser, 11, 13, new Date(System.currentTimeMillis()), false);

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


        List<String> roleNames = new ArrayList<>();
        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testFacilityOwner.setRole(role);
        roleNames.add(testFacilityOwner.getRole().getRoleType().name());

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);


        testFacilityOwner = facilityOwnerService.save(testFacilityOwner);
        token = createTokenByRole(RoleType.ROLE_ADMIN.toString());
        testAddres = addressService.save(testAddres);
        testFacility.setAddress(testAddres);
        testFacilitytype.setId(2);
        testFacility.setFacilityOwner(testFacilityOwner);
        testFacility.setPictures(new ArrayList<>());
        testFacility.setAddress(testAddres);
        testFacility = facilityService.save(testFacility);
        testReservation.setUser(testFacilityOwner);
        testReservation.setFacility(testFacility);
        testReservation = reservationService.save(testReservation);


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
    public void getAllReservationsTest() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations")
                .header("Authorization", token))
                .andExpect(handler().handlerType(ReservationController.class))
                .andExpect(handler().methodName("getAllReservations"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservations);
    }


    @Test
    public void getById() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/{id}", testReservation.getId())
                .header("Authorization", token))
                .andExpect(handler().handlerType(admin.controllers.ReservationController.class))
                .andExpect(handler().methodName("getReservationById"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Reservation reservation = objectMapper.readValue(result.getResponse().getContentAsString(), Reservation.class);
        TestCase.assertNotNull("The user == null.", reservation);
    }


    @Test
    public void deleteReservationTest() throws Exception {

        mockMvc.perform(post("/reservations/delete/{id}", testReservation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk());

    }

    @Test
    public void deleteListTest() throws Exception {

        DeleteItems deleteItems = new DeleteItems();
        List<Integer> deletedId = new ArrayList<>();
        deletedId.add(testReservation.getId());
        deleteItems.setItemsId(deletedId);

        String json = objectMapper.writeValueAsString(deleteItems);

        MvcResult result = mockMvc.perform(post("/reservations/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void updateReservation() throws Exception {
        final Date oldDate = testReservation.getDate();
        testReservation.setDate(new Date(System.currentTimeMillis()));

        String json = objectMapper.writeValueAsString(testUser);

        MvcResult result = mockMvc.perform(patch("/reservations/update/{id}", testReservation.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        Reservation updatedReservation = reservationService.findById(testReservation.getId());
        TestCase.assertNotSame(oldDate, updatedReservation.getDate());
    }

    @Test
    public void getByFacilityId() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/facility/{id}", testFacility.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservationService.findById(testReservation.getId()));
    }

    @Test
    public void getByUserId() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/user/{id}", testFacilityOwner.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservationService.findById(testReservation.getId()));
    }

    @Test
    public void getByDateAfter() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/after/{date}", testReservation.getDate())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getByDateBefore() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/before/{date}", testReservation.getDate())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getByDate() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/date/{date}", testReservation.getDate())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getByUserAndFacility() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/{userId}/{facilityId}", testFacilityOwner.getId(), testFacility.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservationService.findById(testReservation.getId()));
    }

    @Test
    public void getByFacilityAndDate() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/findByFacility/{id}/{date}", testFacility.getId(), testReservation.getDate())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservationService.findById(testReservation.getId()));
    }

    @Test
    public void getByUserAndDate() throws Exception {

        MvcResult result = mockMvc.perform(get("/reservations/findByUser/{id}/{date}", testFacilityOwner.getId(), testReservation.getDate())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertNotNull("Get reservationById == null", reservationService.findById(testReservation.getId()));
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
