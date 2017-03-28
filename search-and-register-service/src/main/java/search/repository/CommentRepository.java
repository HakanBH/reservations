package search.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import search.model.Comment;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@RepositoryRestResource(exported = false)
public interface CommentRepository extends CrudRepository<Comment, Integer> {
    List<Comment> findByFacilityIdAndIsDeleted(Integer facility_id, boolean isDeleted);

    List<Comment> findByUserIdAndIsDeleted(Integer user_id, boolean isDeleted);
}
