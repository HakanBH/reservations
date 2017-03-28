package admin.repository;

import admin.model.Picture;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;


/**
 * Created by Hakan_Hyusein on 8/17/2016.
 */
@RepositoryRestResource(exported = false)
public interface PictureRepository extends CrudRepository<Picture, Integer> {

    List<Picture> findByFacilityId(Integer facilityId);
}
