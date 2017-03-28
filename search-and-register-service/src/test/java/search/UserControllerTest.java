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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.NestedServletException;
import search.configuration.security.SearchAndRegisterCorsFilter;
import search.controllers.UserController;
import search.model.*;
import search.model.dto.ChangePasswordDTO;
import search.model.dto.CreateUser;
import search.repository.RoleRepository;
import search.service.persistence.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 11/18/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class UserControllerTest {

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
    private RoleRepository roleRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Authentication authentication;

    private MockMvc mockMvc;

    private String token;


    @Before
    public void setUp() throws Exception {

        token = createTokenByRole(RoleType.ROLE_USER.toString());

        testUser.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        addressService.save(testAddres);
        testFacilitytype.setId(2);
        testFacilityOwner.setAddress(testAddres);

        facilityOwnerService.save(testFacilityOwner);

        testFacility.setFacilityOwner(testFacilityOwner);
        facilityService.save(testFacility);

        testReservation.setFacility(testFacility);
        testReservation.setUser(testUser);

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
    public void getAllUsers() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testUser.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        testUser.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

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

        if (testUser.getId() == null) {
            userService.save(testUser);
        }

        MvcResult result = mockMvc.perform(get("/users/{id}", testUser.getId())
                .header("Authorization", token))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("getUserById"))
                .andExpect(status().isOk())
                .andReturn();
        User user = objectMapper.readValue(result.getResponse().getContentAsString(), User.class);

        TestCase.assertNotNull("The user == null.", user);
    }

    @Test
    public void createUserTest() throws Exception {

        CreateUser user = new CreateUser();
        user.setFirstName(testUser.getFirstName());
        user.setLastName(testUser.getLastName());
        user.setEmail(testUser.getEmail());
        user.setPassword(testUser.getPassword());

        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("createAndVerifyUser"))
                .andExpect(status().isCreated());
    }

    @Test(expected = NestedServletException.class)
    public void createUserThrowEmptyPassException() throws Exception {

        User user = testUser;
        user.setPassword(null);

        try {
            String json = objectMapper.writeValueAsString(user);
            MvcResult result = mockMvc.perform(post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .content(json))
                    .andExpect(handler().handlerType(UserController.class))
                    .andExpect(handler().methodName("createUser"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (IllegalArgumentException e) {
            throw new NestedServletException("Password missing !");
        }
    }

    @Test
    public void createUserThrowExistingUserException() throws Exception {

        userService.save(testUser);
        User existingUser = userService.findByEmail(testUser.getEmail());
        CreateUser user = new CreateUser();
        user.setFirstName(existingUser.getFirstName());
        user.setLastName(existingUser.getLastName());
        user.setEmail(existingUser.getEmail());
        user.setPassword(existingUser.getPassword());

        String json = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("createAndVerifyUser"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    public void updateUser() throws Exception {
        userService.save(testUser);
        User user = userService.findByEmail(testUser.getEmail());
        final String oldPhone = user.getPhoneNumber();
        user.setPhoneNumber("0898654321");

        String json = objectMapper.writeValueAsString(user);

        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test(expected = NullPointerException.class)
    public void deleteUserTest() throws Exception {

        testUser.setRole(roleRepository.findByRoleType(RoleType.ROLE_ADMIN));

        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.save(testUser);
        User testUser = userService.findByEmail(this.testUser.getEmail());
        mockMvc.perform(post("/users/{Id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("deleteUser"));

        User deletedUser = userService.findById(testUser.getId());

        if (deletedUser.isDeleted()) {
            throw new NullPointerException();
        } else {
            TestCase.assertNotNull("User not deleted", deletedUser);
        }
    }

    @Test
    public void getUserReservations() throws Exception {

        Reservation reservation = new Reservation();
        reservation.setFromHour(10);
        reservation.setToHour(11);
        reservation.setDeposit(true);
        reservation.setDate(new Date(System.currentTimeMillis()));
        reservation.setUser(testUser);
        reservation.setFacility(facilityService.findById(testFacility.getId()));

        List<Reservation> testUserReservations = new ArrayList<>();
        testUserReservations.add(reservation);

        testUser.setReservations(testUserReservations);
        userService.save(testUser);
//        User testUser = userService.findByEmail(this.testUser.getEmail());

        MvcResult result = mockMvc.perform(get("/users/{id}/reservations", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("getUserReservations"))
                .andExpect(status().isOk())
                .andReturn();

        List<Reservation> reservations = Arrays.asList(objectMapper.readValue(result.getResponse().getContentAsString(), Reservation[].class));

        TestCase.assertNotNull("User doesn't have reservations.", reservations);
    }

    @Test
    public void changeUserPassword() throws Exception {

        userService.save(testUser);
        User testUser = userService.findByEmail(this.testUser.getEmail());
        ChangePasswordDTO changePass = new ChangePasswordDTO();
        changePass.setOldPassword(this.testUser.getPassword());
        changePass.setNewPassword(this.testUser.getPassword() + "!");
        changePass.setConfirmPassword(this.testUser.getPassword() + "!");

        String json = objectMapper.writeValueAsString(changePass);

        final String OLD_PASS = testUser.getPassword();

        MvcResult result = mockMvc.perform(put("/users/changepass/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(UserController.class))
                .andExpect(handler().methodName("changeUserPassword"))
                .andReturn();

        User updatedUser = userService.findById(testUser.getId());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        TestCase.assertTrue("The password isn't changed.", !passwordEncoder.matches(OLD_PASS, updatedUser.getPassword()));
        TestCase.assertFalse("The password was changed successfully.", passwordEncoder.matches(OLD_PASS, updatedUser.getPassword()));

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
