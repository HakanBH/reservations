package search.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import search.model.Address;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/20/2016.
 */
@Repository
public interface AddressRepository extends CrudRepository<Address, Integer> {
    List<Address> findByNeighbourhood(String neighbourhood);
}
