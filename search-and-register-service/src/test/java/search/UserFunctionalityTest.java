package search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import search.exceptions.ExistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.model.User;
import search.service.persistence.UserService;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Trayan_Muchev on 8/17/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class UserFunctionalityTest {

    @Autowired
    private UserService userService;

    private User user;

    @Before
    public void setUp() {
        try {
            user = userService.save(new User("trayan95@abv.bg", "Trayan", "Muchev", "password", "0884300666"));
        } catch (ExistingUserException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Test1Save() {
        assertNotNull(user);
    }

    @Test
    public void Test2FindOne() {
        user = userService.findById(user.getId());
        assertNotNull(user);
    }

    @Test
    public void Test3FindAll() {
        List<User> users = userService.findAll();
        assertNotNull(users);
    }

    @Test
    public void Test4FindByEmailAndPassword() {
        try {
            user = userService.findByEmailAndPassword(user.getEmail(), "password");
        } catch (WrongEmailOrPassword wrongEmailOrPassword) {
            wrongEmailOrPassword.printStackTrace();
        }
        assertNotNull(user);
    }

    @Test
    public void Test5FindByEmail() {
        user = userService.findByEmail(user.getEmail());
        assertNotNull(user);
    }

    @Test
    public void Test6Update() {
        user.setFirstName("Hakan");
        user = userService.updateUserById(user.getId(), user);
        assertEquals("Hakan", user.getFirstName());
    }

    @Test
    public void Test8Delete() {
        userService.deleteUser(user.getId());
        user = userService.findById(user.getId());
        assertTrue(user.isDeleted());
    }

    public void getUserByIdSecurityTest() {

    }
}
