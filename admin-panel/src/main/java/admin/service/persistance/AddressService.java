package admin.service.persistance;


import admin.model.Address;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/20/2016.
 */
public interface AddressService {
    Address save(Address address);

    List<Address> findByNeighbourhood(String neighbourhood);

    Address findById(int id);

    Address updateAddress(Address a);

    List<Address> findAll();

    void delete(Integer addressId);
}
