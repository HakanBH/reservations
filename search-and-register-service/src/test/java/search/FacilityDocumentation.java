package search;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import search.UsersDocumentation.ConstrainedFields;
import search.configuration.security.SearchAndRegisterCorsFilter;
import search.exceptions.ExistingUserException;
import search.model.*;
import search.repository.FacilityTypeRepository;
import search.repository.RoleRepository;
import search.service.persistence.AddressService;
import search.service.persistence.FacilityOwnerService;
import search.service.persistence.FacilityService;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by Hakan_Hyusein on 8/4/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class FacilityDocumentation {
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    @Autowired
    private JsonWebTokenUtility tokenUtility;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private FacilityService facilityService;
    @Autowired
    private FacilityOwnerService facilityOwnerService;
    @Autowired
    private FacilityTypeRepository facilityTypeRepository;
    @Autowired
    private AddressService addressService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FacilityOwnerService ownerService;
    @Autowired
    private SearchAndRegisterCorsFilter searchAndRegisterCorsFilter;

    private String token;
    private Address testAddress = new Address("Bulgaria", "Yambol", "Kargon", "Kargonaftika 14");
    private FacilityOwner testFacilityOwner = new FacilityOwner("test1111@test.com", "Ant", "Antonov", "1234", "0898123456");
    private FacilityType testFacilityType = new FacilityType("Tennis");
    private Facility testFacility = new Facility(testAddress, testFacilityOwner, testFacilityType, 11, "Cola Tennis cort");
    private Authentication authentication;
    private MockMvc mockMvc;


    @Before
    public void setUp() {

        testFacilityOwner.setRole(roleRepository.findByRoleType(RoleType.ROLE_FACILITY_OWNER));
        addressService.save(testAddress);
        testFacilityOwner.setAddress(testAddress);
        ownerService.save(testFacilityOwner);

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

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation))
                .build();
    }

    @Test
    public void facilityCreateExample() throws Exception {
        Facility facility = createTestFacility();

        ConstrainedFields fields = new ConstrainedFields(Facility.class);

        this.mockMvc
                .perform(post("/facilities")
                        .contentType(MediaTypes.HAL_JSON)
                        .content(this.objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(facility))
                        .header("Authorization", token))
                .andExpect(
                        status().isOk())
                .andDo(document("facilities-create",
                        requestFields(
                                fields.withPath("facilityType").description("Type of the facility."),
                                fields.withPath("facilityOwner").description("The owner of the given facility."),
                                fields.withPath("price").description("This facility's regular price."),
                                fields.withPath("discountPrice").description("Discount prices"),
                                fields.withPath("weekendsPrice").description("This facility's weekend price."),
                                fields.withPath("description").description("Description of the facility."),
                                fields.withPath("weekdayHours").description("This facility's working hours during weekdays."),
                                fields.withPath("weekendHours").description("This facility's working hours during the weekend."),
                                fields.withPath("address").description("Address of the facility."),
                                fields.withPath("rating").description("The rating of the facility."),
                                fields.withPath("comments").description("Facility comments.").ignored(),
                                fields.withPath("reservations").description("Facility reservations.").ignored(),
                                fields.withPath("pictures").description("Facility pictures.").ignored(),
                                fields.withPath("deleted").description("Is Facility deleted.").ignored(),
                                fields.withPath("name").description("Facility name."),
                                fields.withPath("id").description("Facility id.").ignored(),
                                fields.withPath("numberOfUserReservations").type(Object.class).description("numberOfUserReservations .").ignored()

                        )));
    }

    @Test
    public void getFacilityByIdExample() throws Exception {

        ConstrainedFields fields = new ConstrainedFields(Facility.class);

        Facility facility = createTestFacility();
        facilityService.save(facility);

        this.mockMvc
                .perform(get("/facilities/" + facility.getId())
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andDo(document("facility-get",
                        links(halLinks(),
                                linkWithRel("self").description("This <<resources-facility, Facility resource>>").optional(),
                                linkWithRel("facility-comments").description("Comments made for this facility.").optional(),
                                linkWithRel("facility-reservations").description("Reservations made for this facility.").optional()),
                        responseFields(
                                fields.withPath("id").description("Id of the facility .").optional(),
                                fields.withPath("name").description("Name of the facility .").optional(),
                                fields.withPath("comments").description("Comments of the facility .").optional(),
                                fields.withPath("reservations").description("Reservations of the facility .").optional(),
                                fields.withPath("pictures").description("Pictures of the facility .").optional(),
                                fields.withPath("facilityOwner").description("facilityOwner of the facility .").optional(),
                                fields.withPath("price").description("Price of the facility (per hour)."),
                                fields.withPath("discountPrice").description("Discount prices."),
                                fields.withPath("weekendsPrice").description("Weekend price of the facility."),
                                fields.withPath("facilityType").description("Type of the facility."),
                                fields.withPath("description").description("Extra info for the facility."),
                                fields.withPath("weekendHours").description("Weekend working hours of the facility."),
                                fields.withPath("weekdayHours").description("Weekday working hours of the facility."),
                                fields.withPath("address").description("Address of the facility."),
                                fields.withPath("deleted").description("Is the facility deleted.").optional(),
                                fields.withPath("rating").description("Rating of the facility ([0,5])."),
                                fields.withPath("numberOfUserReservations").type(Object.class).description("numberOfUserReservations .").ignored()

                        )));
    }

    @Test
    public void facilityUpdateExample() throws Exception {
        ConstrainedFields fields = new ConstrainedFields(Facility.class);
        Facility facility = createTestFacility();
        facilityService.save(facility);


        Facility updatedFacility = new Facility();

        facility.setRating(2.0);
        facility.setWeekendsPrice(333);
        facility.setDiscountPrice(555);
        facility.setPrice(333);
        facility.setDescription("Updated description");
        facility.setFacilityType(facilityTypeRepository.findOne(4));
        String json = objectMapper.writeValueAsString(updatedFacility);

        this.mockMvc
                .perform(patch("/facilities/update/{id}", facility.getId())
                        .header("Authorization", token)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andDo(document("facility-update",
                        requestFields(
                                fields.withPath("facilityType").description("Type of the facility."),
                                fields.withPath("facilityOwner").description("The owner of the given facility."),
                                fields.withPath("price").description("This facility's regular price."),
                                fields.withPath("discountPrice").description("This facility's price after 17:00"),
                                fields.withPath("weekendsPrice").description("This facility's weekend price."),
                                fields.withPath("description").description("Description of the facility."),
                                fields.withPath("weekdayHours").description("This facility's working hours during weekdays."),
                                fields.withPath("weekendHours").description("This facility's working hours during the weekend."),
                                fields.withPath("address").description("Address of the facility."),
                                fields.withPath("rating").description("The rating of the facility."),

                                fields.withPath("name").description("Name of the facility .").optional(),
                                fields.withPath("comments").description("Comments of the facility .").optional(),
                                fields.withPath("reservations").description("Reservations of the facility .").optional(),
                                fields.withPath("pictures").description("Pictures of the facility .").optional(),
                                fields.withPath("facilityOwner").description("facilityOwner of the facility .").optional(),
                                fields.withPath("deleted").description("Is the facility deleted.").optional(),
                                fields.withPath("id").description("Facility id.").ignored(),
                                fields.withPath("numberOfUserReservations").type(Object.class).description("numberOfUserReservations .").ignored()

                        )));
    }

    public Facility createTestFacility() throws ExistingUserException {
        Facility testFacility = new Facility();
        testFacility.setWeekdayHours(new WorkingHours(Short.valueOf("8"), Short.valueOf("0")));
        testFacility.setWeekendHours(new WorkingHours(Short.valueOf("10"), Short.valueOf("22")));
        Address a = new Address("Bulgaria", "Sofia", "Goce Delchev", "gen. Kiril Botev 1");
        testFacility.setAddress(a);
        testFacility.setDescription("This is a description of the facility.");
        testFacility.setFacilityType(facilityTypeRepository.findOne(1));
        testFacility.setPrice(123);
        testFacility.setDiscountPrice(234);
        testFacility.setWeekendsPrice(123);
//        testFacility.setRating(5);

        //create a test facility owner
        FacilityOwner facilityOwner = facilityOwnerService.findByEmail("owner@gmail.com");
        if (facilityOwner == null) {
            facilityOwner = new FacilityOwner("owner@gmail.com", "Hakan", "Hyusein", "pass", "0887989199");
            facilityOwnerService.save(facilityOwner);
        }
        testFacility.setFacilityOwner(facilityOwner);

        return testFacility;
    }

    private String createTokenByRole(String role) {
        AuthTokenDetailsDTO detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "11111";
        detailsDTO.email = testFacilityOwner.getEmail();
        List<String> roles = new ArrayList<>();
        roles.add(role);
        detailsDTO.roleNames = roles;

        return tokenUtility.createJsonWebToken(detailsDTO);
    }
}
