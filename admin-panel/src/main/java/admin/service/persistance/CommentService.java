package admin.service.persistance;


import admin.model.Comment;

import java.util.List;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
public interface CommentService {

    Comment save(Comment comment);

    List<Comment> findByFacilityId(Integer facilityId);

    List<Comment> findByUserId(Integer userId);

    void deleteComment(Integer id);

    List<Comment> deleteList(List<Integer> commentsId);

    Comment update(Integer id, Comment comment);

    Comment findById(Integer commentId);

    List<Comment> findAll();
}
