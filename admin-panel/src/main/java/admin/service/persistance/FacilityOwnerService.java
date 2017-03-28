package admin.service.persistance;

import admin.exceptions.ExistingUserException;
import admin.model.FacilityOwner;

import java.util.List;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
public interface FacilityOwnerService {

    FacilityOwner save(FacilityOwner owner) throws ExistingUserException;

    FacilityOwner findById(int id);

    List<FacilityOwner> findAll();

    FacilityOwner updateOwner(FacilityOwner facilityOwner);

    void deleteUser(int id);

    List<FacilityOwner> deleteList(List<Integer> ownersId);

    List<FacilityOwner> findAllByOrderByRatingDesc();

}
