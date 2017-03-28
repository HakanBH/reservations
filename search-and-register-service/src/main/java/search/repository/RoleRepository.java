package search.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import search.model.Role;
import search.model.RoleType;

/**
 * Created by Hakan_Hyusein on 7/27/2016.
 */
@RepositoryRestResource(exported = false)
public interface RoleRepository extends CrudRepository<Role, Integer> {
    Role findByRoleType(RoleType roleType);
}
