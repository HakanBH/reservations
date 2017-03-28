package admin.repository;

import admin.model.FacilityType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Created by Hakan_Hyusein on 8/3/2016.
 */
@RepositoryRestResource(exported = false)
public interface FacilityTypeRepository extends CrudRepository<FacilityType, Integer> {
    public FacilityType findByType(String type);
}
