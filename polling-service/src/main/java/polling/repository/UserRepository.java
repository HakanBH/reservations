package polling.repository;

import org.springframework.data.repository.CrudRepository;
import polling.model.User;

/**
 * Created by Toncho_Petrov on 1/4/2017.
 */
public interface UserRepository extends CrudRepository<User, Integer> {

}
