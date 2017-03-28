package search;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import search.model.Address;
import search.service.persistence.AddressService;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by Trayan_Muchev on 8/16/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SearchAndRegisterApplication.class)
@WebAppConfiguration
@Transactional
public class AddressFunctionalityTest {

    @Autowired
    private AddressService addressService;

    private Address address;

    @Before
    public void setUp() {
        address = addressService.save(new Address("Bulgaria", "Sofia", "JK St Grad", "ul. Tri Ushi"));
    }

    @Test
    public void Test1Save() {
        assertNotNull(address);
    }

    @Test
    public void Test3FindAll() {
        List<Address> addresses = addressService.findAll();
        assertNotNull(addresses);
    }

    @Test
    public void Test4FindOne() {
        address = addressService.findById(address.getId());
        assertNotNull(address);
    }

    @Test
    public void Test5FindByNeighbourhood() {
        List<Address> addresses = addressService.findByNeighbourhood(address.getNeighbourhood());
        assertNotNull(addresses);
    }

    @Test
    public void Test6Delete() {
        addressService.delete(address.getId());
        address = addressService.findById(address.getId());
        assertNull(address);
    }
}
