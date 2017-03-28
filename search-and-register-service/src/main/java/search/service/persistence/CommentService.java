package search.service.persistence;

import search.model.Comment;
import search.model.Facility;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
public interface CommentService {

    Comment save(Comment comment);

    List<Comment> findByFacilityId(Integer facilityId);

    List<Comment> findByUserId(Integer userId);

    Facility deleteComment(Integer id);

    Comment update(Comment comment);

    Comment findById(Integer commentId);
}
