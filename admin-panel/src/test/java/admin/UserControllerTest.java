package admin;

import admin.controllers.UserController;
import admin.model.*;
import admin.repository.RoleRepository;
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
import org.springframework.test.util.AssertionErrors;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 12/16/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AdminApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("admin.controllers")
@EnableScheduling
public class UserControllerTest {

    private User testUser = new User("test1111@test.com", "Ant", "Antonov", "1234", "0887981231");
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
    private FacilityOwnerService ownerService;

    @Autowired
    private FacilityService facilityService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private FacilityOwnerService facilityOwnerService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    private Authentication authentication;

    private MockMvc mockMvc;

    private String token;


    @Before
    public void setUp() throws Exception {


        testUser.setRole(roleRepository.findByRoleType(RoleType.ROLE_ADMIN));

        addressService.save(testAddres);
        testFacilitytype.setId(2);
        testFacilityOwner.setAddress(testAddres);

        facilityOwnerService.save(testFacilityOwner);

        testFacility.setFacilityOwner(testFacilityOwner);
        facilityService.save(testFacility);

        testReservation.setFacility(testFacility);
        testReservation.setUser(testUser);

        token = createTokenByRole(RoleType.ROLE_ADMIN.toString());
        testFacilityOwner.setPassword("!QWERTY&");

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
    public void getAllUsers() throws Exception {

        String adminToken = createTokenByRole(RoleType.ROLE_ADMIN.toString());

        MvcResult result = mockMvc.perform(get("/users")
                .header("Authorization", adminToken))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("all"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> users = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), User[].class));
        TestCase.assertNotNull("No any users.", users);
    }

    @Test
    public void getAllUsersUnauthorizedTest() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ANONYMOUS);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult result = mockMvc.perform(get("/users")
                .header("Authorization", token))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("all"))
                .andExpect(status().isForbidden())
                .andReturn();

    }

    @Test
    public void getUserById() throws Exception {

        userService.save(testUser);
        User newUser = userService.findByEmail(testUser.getEmail());

        MvcResult result = mockMvc.perform(get("/users/{id}", newUser.getId())
                .header("Authorization", token))
                .andExpect(handler().handlerType(admin.controllers.UserController.class))
                .andExpect(handler().methodName("getUserById"))
                .andExpect(status().isOk())
                .andReturn();
        User user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);
        TestCase.assertNotNull("The user == null.", user);
    }

    @Test
    public void updateUser() throws Exception {

        userService.save(testUser);

        User user = userService.findByEmail(testUser.getEmail());
        final String oldPhone = user.getPhoneNumber();
        user.setPhoneNumber("0898654321");

        String json = objectMapper.writeValueAsString(user);

        MvcResult result = mockMvc.perform(patch("/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("updateUser"))
                .andExpect(status().isOk())
                .andReturn();
        User updatedUser = userService.findById(user.getId());
        if (user.getId().equals(updatedUser.getId())
                && user.getEmail().equals(updatedUser.getEmail())
                && updatedUser.getId().equals(updatedUser.getId())
                && !oldPhone.equals(updatedUser.getPhoneNumber())) {
        } else {
            AssertionErrors.fail("Update fail users are equals!");
        }
    }

    @Test(expected = NullPointerException.class)
    public void deleteUserTest() throws Exception {

        userService.save(testUser);
        User testUser = userService.findByEmail(this.testUser.getEmail());
        mockMvc.perform(post("/users/delete/{Id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("deleteUser"))
                .andExpect(status().isOk());

        User deletedUser = userService.findByEmail(testUser.getEmail());
        if (deletedUser.isDeleted()) {
            throw new NullPointerException();
        } else {
            TestCase.assertNotNull("User is not deleted", deletedUser);
        }

    }

    @Test(expected = NullPointerException.class)
    public void deleteListTest() throws Exception {

        userService.save(testUser);
        DeleteItems deleteItems = new DeleteItems();
        List<Integer> deletedId = new ArrayList<>();
        User testUser = userService.findByEmail(this.testUser.getEmail());
        deletedId.add(testUser.getId());
        deleteItems.setItemsId(deletedId);

        String json = objectMapper.writeValueAsString(deleteItems);

        MvcResult result = mockMvc.perform(post("/users/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("deleteList"))
                .andExpect(status().isOk())
                .andReturn();

        List<User> users = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), User[].class));
        User deletedUser = userService.findByEmail(testUser.getEmail());
        if (deletedUser.isDeleted()) {
            throw new NullPointerException();
        } else {
            TestCase.assertNotNull("User is not deleted", deletedUser);
        }

    }

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
