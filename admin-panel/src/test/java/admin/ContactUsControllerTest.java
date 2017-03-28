package admin;

import admin.controllers.ContactUsController;
import admin.exceptions.ExistingUserException;
import admin.model.DeleteItems;
import admin.model.FacilityOwner;
import admin.model.Role;
import admin.model.RoleType;
import admin.service.persistance.FacilityOwnerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Trayan_Muchev on 1/12/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(AdminApplication.class)
@WebAppConfiguration
@Transactional
@ComponentScan("admin.controllers")
@EnableScheduling
public class ContactUsControllerTest {

    private FacilityOwner testUser = new FacilityOwner("test1111@test.com", "Ant", "Antonov", "1234", "0887981231");


    @Autowired
    private WebApplicationContext context;

    @Autowired
    private FacilityOwnerService ownerService;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private ObjectMapper objectMapper;


    private Authentication authentication;

    private MockMvc mockMvc;

    private String token;

    @Before
    public void setUp() throws ExistingUserException {
        List<String> roleNames = new ArrayList<>();
        Role role = new Role();
        role.setRoleType(RoleType.ROLE_ADMIN);
        testUser.setRole(role);
        roleNames.add(testUser.getRole().getRoleType().name());
        authentication = new UsernamePasswordAuthenticationToken(testUser.getUsername(), testUser.getPassword(), testUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        testUser = ownerService.save(testUser);
        token = createTokenByRole(RoleType.ROLE_ADMIN.toString());

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());


        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .build();


    }


    @Test
    public void getAllMessagesTest() throws Exception {

        MvcResult result = mockMvc.perform(get("/messages")
                .header("Authorization", token))
                .andExpect(handler().handlerType(ContactUsController.class))
                .andExpect(handler().methodName("getMessages"))
                .andExpect(status().isOk())
                .andReturn();


    }


    @Test
    public void deleteListTest() throws Exception {

        DeleteItems deleteItems = new DeleteItems();
        List<Integer> deletedIds = new ArrayList<>();
        deleteItems.setItemsId(deletedIds);

        String json = objectMapper.writeValueAsString(deleteItems);

        MvcResult result = mockMvc.perform(post("/messages/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .header("Authorization", token))
                .andExpect(handler().handlerType(ContactUsController.class))
                .andExpect(handler().methodName("deleteList"))
                .andExpect(status().isOk())
                .andReturn();


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
