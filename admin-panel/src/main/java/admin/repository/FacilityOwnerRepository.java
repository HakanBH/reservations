package admin.repository;

import admin.model.FacilityOwner;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@Repository
public interface FacilityOwnerRepository extends CrudRepository<FacilityOwner, Integer> {
    FacilityOwner findByEmail(String email);

    List<FacilityOwner> findAllByOrderByRatingDesc();

    FacilityOwner findByEmailAndPassword(String email, String password);
}
