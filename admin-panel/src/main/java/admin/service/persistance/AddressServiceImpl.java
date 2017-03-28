package admin.service.persistance;


import admin.model.Address;
import admin.repository.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Created by Trayan_Muchev on 7/20/2016.
 */

@Service("addressService")
@Transactional
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    @Override
    public List<Address> findByNeighbourhood(String neighbourhood) {
        return addressRepository.findByNeighbourhood(neighbourhood);
    }

    @Override
    public Address findById(int id) {
        return addressRepository.findOne(id);
    }

    @Override
    public Address updateAddress(Address a) {
        if (a.getId() != null) {
            Address entity = addressRepository.findOne(a.getId());
            if (entity != null) {
                entity.setCountry(a.getCountry());
                entity.setCity(a.getCity());
                entity.setNeighbourhood(a.getNeighbourhood());
                entity.setStreet(a.getStreet());
                entity.setLng(a.getLng());
                entity.setLat(a.getLat());
                addressRepository.save(entity);
                return entity;
            }
        }
        return addressRepository.save(a);
    }

    @Override
    public List<Address> findAll() {
        return (List<Address>) addressRepository.findAll();
    }

    @Override
    public void delete(Integer addressId) {
        addressRepository.delete(addressId);
    }
}
