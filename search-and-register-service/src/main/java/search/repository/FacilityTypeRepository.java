package search.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import search.model.FacilityType;

/**
 * Created by Hakan_Hyusein on 8/3/2016.
 */
@RepositoryRestResource(exported = false)
public interface FacilityTypeRepository extends CrudRepository<FacilityType, Integer> {
    public FacilityType findByType(String type);
}
