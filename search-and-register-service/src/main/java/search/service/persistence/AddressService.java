package search.service.persistence;

import search.model.Address;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/20/2016.
 */
public interface AddressService {
    Address save(Address address);

    List<Address> findByNeighbourhood(String neighbourhood);

    Address findById(int id);

    Address updateAddress(Address entity, Address newAddress);

    List<Address> findAll();

    void delete(Integer addressId);
}
