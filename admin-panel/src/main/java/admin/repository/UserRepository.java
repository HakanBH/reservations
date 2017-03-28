package admin.repository;


import admin.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;


/**
 * Created by Toncho_Petrov on 7/13/2016.
 */
@Repository
@RestResource(path = "users")
public interface UserRepository extends CrudRepository<User, Integer> {
    User findByEmailAndPassword(String email, String password);

    User findByEmail(String email);

    User findByGoogleEmail(String email);

    User findByFacebookEmail(String email);
}
