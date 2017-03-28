package admin.repository;

import admin.model.UserMessage;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Hakan_Hyusein on 11/28/2016.
 */
@Repository
public interface UserMessageRepository extends CrudRepository<UserMessage, Integer> {
}
