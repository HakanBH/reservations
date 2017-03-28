package search.repository;


import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import search.model.User;

import java.util.List;


/**
 * Created by Toncho_Petrov on 7/13/2016.
 */
@RepositoryRestResource(path = "users")
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findByGoogleEmail(String email);

    User findByFacebookEmail(String email);

    List<User> findByIsDeleted(boolean facilityIsDeleted);
}
