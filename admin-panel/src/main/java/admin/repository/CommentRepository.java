package admin.repository;

import admin.model.Comment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@RepositoryRestResource(exported = false)
public interface CommentRepository extends CrudRepository<Comment, Integer> {
    List<Comment> findByFacilityId(Integer facility_id);

    List<Comment> findByUserId(Integer user_id);
}
