package search.service.persistence;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.model.Address;
import search.repository.AddressRepository;

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
    public Address updateAddress(Address entity, Address newAddress) {
        if (entity != null) {
            entity.setCountry(newAddress.getCountry());
            entity.setCity(newAddress.getCity());
            entity.setNeighbourhood(newAddress.getNeighbourhood());
            entity.setStreet(newAddress.getStreet());
            entity.setLat(newAddress.getLat());
            entity.setLng(newAddress.getLng());
            addressRepository.save(entity);
            return entity;
        }

        return addressRepository.save(newAddress);
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
