package reservations.repository;

import org.springframework.data.repository.CrudRepository;
import reservations.model.User;

public interface UserRepository extends CrudRepository<User, Integer> {
}
