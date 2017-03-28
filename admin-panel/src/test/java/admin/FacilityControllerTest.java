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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Trayan_Muchev on 1/11/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AdminApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("admin.controllers")
public class FacilityControllerTest {

    private Long time = System.currentTimeMillis();
    private Address testAddress = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("owner12345@email.com", "Cola", "Dragicheva", "ciciCh", "0898123456");
    private FacilityType testFacilitytype = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddress, testFacilityOwner, testFacilitytype, 11, "Cola Tennis cort");


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
        testFacilitytype.setId(2);
        testFacility.setFacilityOwner(testFacilityOwner);
        testFacility.setAddress(testAddress);
        testFacility.setFacilityType(testFacilitytype);
        testFacility.setPictures(new ArrayList<>());
        testFacility = facilityService.save(testFacility);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());


        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();

    }


    @Test
    public void getAllFacilitiesTest() throws Exception {

        MvcResult result = mockMvc.perform(get("/facilities")
                .header("Authorization", token)
                .accept(MediaType.parseMediaType("application/json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andReturn();

        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("No any users.", facilities);
    }

    @Test
    public void getFacilityById() throws Exception {

        MvcResult result = mockMvc.perform(get("/facilities/{id}", testFacility.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        Facility facility = objectMapper.readValue(result.getResponse().getContentAsString(), Facility.class);
        TestCase.assertNotNull("The facility == null.", facility);
    }


    @Test
    public void updateFacility() throws Exception {

        String oldName = testFacility.getName();
        testFacility.setName("Vitosha");

        String json = objectMapper.writeValueAsString(testFacility);

        MvcResult result = mockMvc.perform(patch("/facilities/update/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(status().isOk())
                .andReturn();
        Facility facility = facilityService.findById(testFacility.getId());
        TestCase.assertEquals(testFacility.getName(), facility.getName());
    }

    @Test
    public void getFacilityComments() throws Exception {

        MvcResult result = mockMvc.perform(get("/facilities/{id}/comments", testFacility.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Comment> comments = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Comment[].class));
        TestCase.assertTrue("The comments == null.", comments.isEmpty());
    }

    @Test
    public void getFacilityReservations() throws Exception {

        MvcResult result = mockMvc.perform(get("/facilities/{id}/reservations", testFacility.getId())
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));
        TestCase.assertTrue("The comments == null.", reservations.isEmpty());
    }

    @Test
    public void deleteFacilityTest() throws Exception {

        mockMvc.perform(post("/facilities/delete/{id}", testFacility.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk());

        Facility deletedFacility = facilityService.findById(testFacility.getId());
        TestCase.assertTrue(deletedFacility.isDeleted());

    }

    @Test
    public void deleteListTest() throws Exception {

        DeleteItems deleteItems = new DeleteItems();
        List<Integer> deletedId = new ArrayList<>();
        deletedId.add(testFacility.getId());
        deleteItems.setItemsId(deletedId);

        String json = objectMapper.writeValueAsString(deleteItems);

        MvcResult result = mockMvc.perform(post("/facilities/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();

        Facility deletedFacility = facilityService.findById(testFacility.getId());
        TestCase.assertTrue(deletedFacility.isDeleted());


    }

    @Test
    public void getByWeekendPeriodOfTime() throws Exception {

        WorkingHours weekendHours = new WorkingHours();
        weekendHours.setStartHour((short) 4);
        weekendHours.setEndHour((short) 5);

        String json = objectMapper.writeValueAsString(weekendHours);

        MvcResult result = mockMvc.perform(post("/facilities/weekendPeriodOfTime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getByWeekdayPeriodOfTime() throws Exception {

        WorkingHours weekdayHours = new WorkingHours();
        weekdayHours.setStartHour((short) 4);
        weekdayHours.setEndHour((short) 5);

        String json = objectMapper.writeValueAsString(weekdayHours);

        MvcResult result = mockMvc.perform(post("/facilities/weekendPeriodOfTime")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }


    @Test
    public void getFacilityByAddress() throws Exception {

        Address address = new Address();
        address.setCity(testAddress.getCity());
        address.setCountry(testAddress.getCountry());
        address.setNeighbourhood(testAddress.getNeighbourhood());
        address.setStreet(testAddress.getStreet());

        String json = objectMapper.writeValueAsString(address);

        MvcResult result = mockMvc.perform(post("/facilities/getByAddress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void getByOwner() throws Exception {


        MvcResult result = mockMvc.perform(get("/facilities/getByOwner/{id}", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("The comments == null.", facilities);
    }

    @Test
    public void getByFacilityType() throws Exception {


        MvcResult result = mockMvc.perform(get("/facilities/getByType/{type}", testFacility.getFacilityType().getType().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("The comments == null.", facilities);
    }

    @Test
    public void getByPrice() throws Exception {


        MvcResult result = mockMvc.perform(get("/facilities/getByPrice/{price}", testFacility.getPrice())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("The comments == null.", facilities);
    }

    @Test
    public void getByRating() throws Exception {


        MvcResult result = mockMvc.perform(get("/facilities/getByRating")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andReturn();
        List<Facility> facilities = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Facility[].class));
        TestCase.assertNotNull("The comments == null.", facilities);
    }

    @Test
    public void getFacilityTypes() throws Exception {


        MvcResult result = mockMvc.perform(get("/facilities/getFacilityTypes")
                .contentType(MediaType.APPLICATION_JSON)
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
