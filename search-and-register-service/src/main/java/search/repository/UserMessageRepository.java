package search.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import search.model.UserMessage;

/**
 * Created by Hakan_Hyusein on 11/28/2016.
 */
@Repository
public interface UserMessageRepository extends CrudRepository<UserMessage, Integer> {
}
