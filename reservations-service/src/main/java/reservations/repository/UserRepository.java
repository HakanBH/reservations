package reservations.repository;

import org.springframework.data.repository.CrudRepository;
import reservations.model.User;

/**
 * Created by Trayan_Muchev on 9/15/2016.
 */
public interface UserRepository extends CrudRepository<User, Integer> {
}
