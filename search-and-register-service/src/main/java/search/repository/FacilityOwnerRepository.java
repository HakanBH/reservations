package search.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import search.model.FacilityOwner;

import java.util.List;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@Repository
public interface FacilityOwnerRepository extends CrudRepository<FacilityOwner, Integer> {
    FacilityOwner findByEmail(String email);

    List<FacilityOwner> findAllByOrderByRatingDesc();

    List<FacilityOwner> findByIsDeletedOrderByRatingDesc(boolean facilityIsDeleted);

    FacilityOwner findByEmailAndPassword(String email, String password);

    List<FacilityOwner> findByIsDeleted(boolean facilityOwnerIsDeleted);
}
