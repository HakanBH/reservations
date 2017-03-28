package search;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.constraints.ConstraintDescriptions;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import search.configuration.security.SearchAndRegisterCorsFilter;
import search.exceptions.ExistingUserException;
import search.model.Address;
import search.model.RoleType;
import search.model.User;
import search.model.dto.CreateUser;
import search.repository.RoleRepository;
import search.service.persistence.AddressService;
import search.service.persistence.UserService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class UsersDocumentation {
    private User TEST_USER = new User("test1111@test.com", "Ant", "Antonov", "1234", "0887981231");
    private Address TEST_ADDRESS = new Address("Bulgaria", "Sofia", "Goce Delchev", "gen. Kiril Botev 1");
    private String token;


    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SearchAndRegisterCorsFilter searchAndRegisterCorsFilter;

    @Autowired
    private WebApplicationContext context;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private JsonWebTokenUtility tokenUtility;

    private MockMvc mockMvc;
    private Authentication authentication;

    @Before
    public void setUp() {

        TEST_USER.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(TEST_USER.getEmail(), TEST_USER.getPassword());

        SecurityContextHolder.getContext().setAuthentication(principal);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext());

        token = createToken();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .addFilters(searchAndRegisterCorsFilter)
                .build();
    }


    @After
    public void clear() {
        SecurityContextHolder.clearContext();
    }


    @Bean
    public UserDetailsService userDetailsService() {
        GrantedAuthority authority = new SimpleGrantedAuthority("ADMIN");
        UserDetails userDetails = (UserDetails) new User(TEST_USER.getEmail(), TEST_USER.getLastName(), "0898123456", TEST_USER.getRole());
        return new InMemoryUserDetailsManager(Arrays.asList(userDetails));
    }

    public static class MockSecurityContext implements SecurityContext {

        private static final long serialVersionUID = -1386535243513362694L;

        private Authentication authentication;

        public MockSecurityContext(Authentication authentication) {
            this.authentication = authentication;
        }

        @Override
        public Authentication getAuthentication() {
            return this.authentication;
        }

        @Override
        public void setAuthentication(Authentication authentication) {
            this.authentication = authentication;
        }
    }


    @Test
    public void indexExample() throws Exception {

        this.mockMvc.perform(get("/")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(document("index",
                        links(
                                linkWithRel("users").description("The <<resources-users,Users resource>>"),
                                linkWithRel("owners").description("The <<resources-owners,Owners resource>>"),
//                                linkWithRel("reservations").description("The <<resources-reservations,Reservations resource>>"),
                                linkWithRel("facilities").description("The <<resources-facilities,Facilities resource>>")),
                        responseFields(
                                fieldWithPath("_links").description("<<resources-index-links,Links>> to other resources"))));

    }

    @Test
    public void userCreateExample() throws Exception {

        CreateUser user = new CreateUser();
        user.setFirstName(TEST_USER.getFirstName());
        user.setLastName(TEST_USER.getLastName());
        user.setPassword(TEST_USER.getPassword());
        user.setPhone(TEST_USER.getPhoneNumber());
        user.setEmail(TEST_USER.getEmail());

        ConstrainedFields fields = new ConstrainedFields(CreateUser.class);
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);

        this.mockMvc
                .perform(post("/users")
                        .header("Authorization", token)
                        .contentType(MediaTypes.HAL_JSON)
                        .content(json))
                .andExpect(handler().methodName("createAndVerifyUser"))
                .andExpect(status().isCreated())
                .andDo(document("users-create",
                        requestFields(

                                fields.withPath("firstName").description("First name of the user."),
                                fields.withPath("lastName").description("Last name of the user."),
                                fields.withPath("password").description("Users password."),
                                fields.withPath("email").description("The email of the user."),
                                fields.withPath("phoneNumber").description("User's phone number."),
                                fields.withPath("role").description("User role type."),
                                fields.withPath("address").description("User's address."),
                                // The fields below should be empty when creating a user. Thats why
                                // they are marked as ignored.
                                fields.withPath("id").description("Users id.").ignored(),
//                                fields.withPath("comments").description("Users comments."),
//                                fields.withPath("reservations").description("Users reservations."),
                                fields.withPath("timesSkiped").description("Number of times user reserved a facility and didn't go.").optional(),
                                fields.withPath("blocked").description("Is the user blocked.").ignored(),

                                fields.withPath("profilePic").description("User picture."),
                                fields.withPath("googleEmail").type(String.class).description("If user registration is created from G+.").optional(),
                                fields.withPath("googleId").type(String.class).description("If user's google email isn't null he musts have googleId .").optional(),
                                fields.withPath("facebookEmail").type(String.class).description("If user create his registrations from facebook.").optional(),
                                fields.withPath("facebookId").type(String.class).description("If user facebook email isn't null he must have facebookId.").optional(),
                                fields.withPath("facebookPic").type(String.class).description("User facebook picture.").optional(),
                                fields.withPath("deleted").type(Boolean.class).description("Is the user deleted.").optional()
                        )));

    }

//    @Test
//    public void getUsersExample() throws Exception {
//        insertTestUser();
//
//        this.mockMvc
//                .perform(get("/users").header("Authorization",token))
//                .andExpect(status().isOk())
//                .andDo(document("users-get", responseFields(
//                        fieldWithPath("_embedded.users").description("An array of <<resources-user, User resources>>"),
//                        fieldWithPath("_embedded.facilityOwners").description("An array of <<resources-user, FacilityOwner resources>>")
//                )));
//    }

    @Test
    public void getUserByIdExample() throws Exception {

        TEST_USER.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        authentication = new UsernamePasswordAuthenticationToken(TEST_USER.getUsername(), TEST_USER.getPassword(), TEST_USER.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        userService.save(TEST_USER);

        ConstrainedFields fields = new ConstrainedFields(User.class);

        this.mockMvc
                .perform(get("/users/{id}", TEST_USER.getId())
                        .header("Authorization", token))
                .andExpect(handler().methodName("getUserById"))
                .andExpect(status().isOk())
                .andDo(document("user-get",
                        responseFields(
                                fields.withPath("id").description("The id of the user."),
                                fields.withPath("email").description("The email of the user."),
                                fields.withPath("timesSkiped").description("Number of times user reserved a facility and didn't go."),
                                fields.withPath("blocked").description("Is the user blocked."),
                                fields.withPath("firstName").description("First name of the user."),
                                fields.withPath("lastName").description("Last name of the user."),
                                fields.withPath("password").type(String.class).description("Users password.").optional(),
                                fields.withPath("email").description("The email of the user."),
                                fields.withPath("phoneNumber").description("User's phone number."),
                                fields.withPath("role").description("User role type."),
                                fields.withPath("address").type(Address.class).description("User's address.").optional(),

                                fields.withPath("profilePic").description("User picture."),
                                fields.withPath("googleEmail").type(String.class).description("If user registration is created from G+.").optional(),
                                fields.withPath("googleId").type(String.class).description("If user's google email isn't null he musts have googleId .").optional(),
                                fields.withPath("facebookEmail").type(String.class).description("If user create his registrations from facebook.").optional(),
                                fields.withPath("facebookId").type(String.class).description("If user facebook email isn't null he must have facebookId.").optional(),
                                fields.withPath("facebookPic").type(String.class).description("User facebook picture.").optional(),
                                fields.withPath("deleted").type(Boolean.class).description("Is the user deleted.").optional(),
                                fields.withPath("verified").type(Boolean.class).description("Is the user verified.").optional(),
                                fields.withPath("accountNonExpired").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("accountNonLocked").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("credentialsNonExpired").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("enabled").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("authorities").type(Object.class).description("User's authorities.").optional(),
                                fields.withPath("username").type(Object.class).description("User username.").ignored()

                        )));
    }


    @Test
    public void userUpdateExample() throws Exception {

        TEST_USER.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));

        authentication = new UsernamePasswordAuthenticationToken(TEST_USER.getUsername(), TEST_USER.getPassword(), TEST_USER.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ConstrainedFields fields = new ConstrainedFields(User.class);
        User userToUpdate = insertTestUser();

        User user = TEST_USER;
        //Update some data
        user.setFirstName("NewfirsName");
        user.setLastName("NewLastName");

        this.mockMvc
                .perform(patch("/users/" + userToUpdate.getId())
                        .header("Authorization", token)
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        requestFields(
                                fields.withPath("firstName").description("First name of the user."),
                                fields.withPath("lastName").description("Last name of the user."),
                                fields.withPath("password").type(String.class).description("Users password.").optional(),
                                fields.withPath("email").description("The email of the user."),
                                fields.withPath("phoneNumber").description("User's phone number."),
                                fields.withPath("role").description("User role type."),
                                fields.withPath("address").type(Address.class).description("User's address.").optional(),

                                // The fields below should be empty when updating a user. Thats why
                                // they are marked as ignored.
                                fields.withPath("id").description("Users id.").ignored(),
//                                fields.withPath("comments").description("Users comments."),
//                                fields.withPath("reservations").description("Users reservations."),
                                fields.withPath("timesSkiped").description("Number of times user reserved a facility and didn't go.").ignored(),
                                fields.withPath("blocked").description("Is the user blocked.").ignored(),
                                fields.withPath("profilePic").description("User picture."),
                                fields.withPath("googleEmail").type(String.class).description("If user registration is created from G+.").optional(),
                                fields.withPath("googleId").type(String.class).description("If user's google email isn't null he musts have googleId .").optional(),
                                fields.withPath("facebookEmail").type(String.class).description("If user create his registrations from facebook.").optional(),
                                fields.withPath("facebookId").type(String.class).description("If user facebook email isn't null he must have facebookId.").optional(),
                                fields.withPath("facebookPic").type(String.class).description("User facebook picture.").optional(),
                                fields.withPath("deleted").type(Boolean.class).description("Is the user deleted.").optional(),
                                fields.withPath("verified").type(Boolean.class).description("Is the user verified.").optional(),
                                fields.withPath("accountNonExpired").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("accountNonLocked").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("credentialsNonExpired").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("enabled").type(Boolean.class).description("Is the user verified.").ignored(),
                                fields.withPath("authorities").type(Object.class).description("User's authorities.").optional(),
                                fields.withPath("username").type(Object.class).description("User username.").ignored()

                        )));
    }

    private User insertTestUser() throws ExistingUserException {
        if (userService.findByEmail(TEST_USER.getEmail()) != null) {
            return userService.findByEmail(TEST_USER.getEmail());
        }
        addressService.save(TEST_ADDRESS);
        TEST_USER.setRole(roleRepository.findByRoleType(RoleType.ROLE_USER));
        TEST_USER.setAddress(TEST_ADDRESS);
        userService.save(TEST_USER);
        return TEST_USER;
    }


    static class ConstrainedFields {

        private final ConstraintDescriptions constraintDescriptions;

        ConstrainedFields(Class<?> input) {
            this.constraintDescriptions = new ConstraintDescriptions(input);
        }

        FieldDescriptor withPath(String path) {
            return fieldWithPath(path).attributes(key("constraints").value(StringUtils
                    .collectionToDelimitedString(this.constraintDescriptions
                            .descriptionsForProperty(path), ". ")));
        }
    }

    private String createToken() {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "1";
        detailsDTO.email = TEST_USER.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }
}