package admin.repository;


import admin.model.Role;
import admin.model.RoleType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;


/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@RepositoryRestResource(exported = false)
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRoleType(RoleType roleType);
}
