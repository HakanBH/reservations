package search;

import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import search.configuration.security.SearchAndRegisterCorsFilter;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Toncho_Petrov on 11/9/2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
public class TokenFunctionality {

    private AuthTokenDetailsDTO detailsDTO;
    private MockMvc mockMvc;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private SearchAndRegisterCorsFilter searchAndRegisterCorsFilter;
    @Autowired
    private WebApplicationContext context;

    @Test
    public void createTokenTest() {
        detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "1";
        detailsDTO.email = "ivanIvanov@gmail.com";
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        detailsDTO.roleNames = roles;
        detailsDTO.expirationDate = tokenUtility.buildExpirationDate();

        String token = tokenUtility.createJsonWebToken(detailsDTO);
        assertNotNull(token);
    }


    @Test
    public void parseTokenTest() {

        detailsDTO = new AuthTokenDetailsDTO();
        detailsDTO.userId = "1";
        detailsDTO.email = "ivanIvanov@gmail.com";
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");
        detailsDTO.roleNames = roles;
        detailsDTO.expirationDate = tokenUtility.buildExpirationDate();

        String token = tokenUtility.createJsonWebToken(detailsDTO);
        AuthTokenDetailsDTO parsedDetails = tokenUtility.parseAndValidate(token);
        TestCase.assertTrue(detailsDTO.equals(parsedDetails));
    }

}
