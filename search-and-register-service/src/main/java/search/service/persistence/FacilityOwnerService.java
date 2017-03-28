package search.service.persistence;

import search.exceptions.ExistingUserException;
import search.exceptions.WrongEmailOrPassword;
import search.model.FacilityOwner;

import java.util.List;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
public interface FacilityOwnerService {

    FacilityOwner save(FacilityOwner owner) throws ExistingUserException;

    FacilityOwner findById(int id);

    List<FacilityOwner> findAll();

    FacilityOwner findByEmailAndPassword(String email, String password) throws WrongEmailOrPassword;

    FacilityOwner updateOwner(FacilityOwner facilityOwner);

    FacilityOwner findByEmail(String email);

    void deleteUser(int id);

    List<FacilityOwner> findAllByOrderByRatingDesc();

}
