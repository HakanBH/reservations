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
import org.springframework.web.util.NestedServletException;
import search.configuration.security.SearchAndRegisterCorsFilter;
import search.controllers.FacilityOwnerController;
import search.exceptions.ExistingUserException;
import search.model.*;
import search.model.dto.CreateUser;
import search.repository.RoleRepository;
import search.service.persistence.AddressService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Toncho_Petrov on 11/23/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class FacilityOwnerControllerTest {

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
    private AddressService addressService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    private Authentication authentication;

    private MockMvc mockMvc;
    private String token;
    private Address testAddress = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("test1111@test.com", "Ant", "Antonov", "1234", "0898123456");
    private FacilityType testFacilityType = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddress, testFacilityOwner, testFacilityType, 11, "Cola Tennis cort");


    @Before
    public void setUp() {

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_FACILITY_OWNER));
        addressService.save(testAddress);
        testFacilityOwner.setAddress(testAddress);
        ownerService.save(testFacilityOwner);

        testFacility.setFacilityOwner(testFacilityOwner);
        testFacility.setAddress(testAddress);
        facilityService.save(testFacility);

        token = createTokenByRole(testFacilityOwner.getRole().getRoleType().toString());

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
    public void createFacilityOwnerTest() throws Exception {

        CreateUser user = new CreateUser();
        user.setFirstName("Mincho");
        user.setLastName("Praznikov");
        user.setEmail("mincho_praznikov123@epam.com");
        user.setPhone("0898123456");
        user.setPassword("@4cd6f08b");

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_ANONYMOUS));

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json = objectMapper.writeValueAsString(user);
        MvcResult result = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("createAndVerifyOwner"))
                .andExpect(status().isCreated())
                .andReturn();

    }


    @Test(expected = ExistingUserException.class)
    public void createFacilityOwnerAlreadyExistTest() throws Exception {

        ownerService.save(testFacilityOwner);

        String json = objectMapper.writeValueAsString(testFacilityOwner);
        MvcResult result = mockMvc.perform(post("/owners")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("createAndVerifyOwner"))
                .andExpect(status().isCreated())
                .andReturn();

    }


    @Test(expected = NestedServletException.class)
    public void emptyPassCreateFacilityOwnerTest() throws Exception {


        CreateUser user = new CreateUser();
        user.setFirstName(testFacilityOwner.getFirstName());
        user.setLastName(testFacilityOwner.getLastName());
        user.setEmail(testFacilityOwner.getEmail());

        user.setPassword(null);

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_ANONYMOUS));
        ownerService.updateOwner(testFacilityOwner);

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            String json = objectMapper.writeValueAsString(user);
            MvcResult result = mockMvc.perform(post("/owners")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .content(json))
                    .andExpect(handler().handlerType(FacilityOwnerController.class))
                    .andExpect(handler().methodName("createAndVerifyOwner"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (IllegalArgumentException e) {
            throw new NestedServletException(e.getMessage());
        }

    }


    @Test
    public void createFacilityAccessDeniedTest() throws Exception {

        Role role = new Role();
        role.setRoleType(RoleType.ROLE_USER);

        testFacilityOwner.setRole(role);

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String json = objectMapper.writeValueAsString(testFacility);
        MvcResult result = mockMvc.perform(post("/owners/{id}/facilities", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json).contentType(MediaType.APPLICATION_JSON))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("createFacility"))
                .andExpect(status().isForbidden())
                .andReturn();
    }

    @Test
    public void getOwnerById() throws Exception {

        MvcResult result = mockMvc.perform(get("/owners/{id}", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("getOwnerById"))
                .andExpect(status().isOk())
                .andReturn();

        FacilityOwner facilityOwner = objectMapper.readValue(result.getResponse().getContentAsString(), FacilityOwner.class);
    }

    @Test
    public void updateUserTest() throws Exception {

        testFacilityOwner.setFirstName("Cenko");
        testFacilityOwner.setLastName("Cokov");
        String json = objectMapper.writeValueAsString(testFacilityOwner);

        MvcResult result = mockMvc.perform(patch("/owners/{id}", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token)
                .content(json))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("updateUser"))
                .andExpect(status().isOk())
                .andReturn();

        FacilityOwner updatedUser = objectMapper.readValue(result.getResponse().getContentAsString(), FacilityOwner.class);
        TestCase.assertNotSame("Users are equals!", testFacilityOwner, updatedUser);
    }

    @Test
    public void deleteOwnerTest() throws Exception {

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_ADMIN));

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult result = mockMvc.perform(delete("/owners/{id}", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isOk())
                .andReturn();

        Boolean isDeleted = objectMapper.readValue(result.getResponse().getContentAsString(), Boolean.class);
        TestCase.assertTrue("User isn't deleted!", isDeleted);
    }

    @Test
    public void deleteOwnerUnAuthorizedTest() throws Exception {

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));
        testFacility.setFacilityOwner(testFacilityOwner);
        facilityService.save(testFacility);

        authentication = new UsernamePasswordAuthenticationToken(testFacilityOwner.getUsername(), testFacilityOwner.getPassword(), testFacilityOwner.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult result = mockMvc.perform(delete("/owners/{id}", testFacilityOwner.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", token))
                .andExpect(handler().handlerType(FacilityOwnerController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().isForbidden())
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
