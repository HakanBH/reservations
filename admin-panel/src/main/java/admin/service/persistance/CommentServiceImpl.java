package admin.service.persistance;

import admin.model.Comment;
import admin.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.lang.Boolean.TRUE;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@Service("commentService")
@Transactional
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByFacilityId(Integer facilityId) {
        return commentRepository.findByFacilityId(facilityId);
    }

    @Override
    public List<Comment> findByUserId(Integer userId) {
        return commentRepository.findByUserId(userId);
    }

    @Override
    public void deleteComment(Integer id) {
        Comment comment = commentRepository.findOne(id);
        comment.setDeleted(TRUE);
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> deleteList(List<Integer> commentsId) {
        for (Integer id : commentsId) {
            Comment comment = commentRepository.findOne(id);
            comment.setDeleted(TRUE);
            commentRepository.save(comment);
        }
        return (List<Comment>) commentRepository.findAll();
    }


    @Override
    public Comment update(Integer id, Comment comment) {
        Comment entity = commentRepository.findOne(comment.getId());

        entity.setText(comment.getText());
        return commentRepository.save(entity);
    }

    @Override
    public Comment findById(Integer commentId) {
        return commentRepository.findOne(commentId);
    }

    @Override
    public List<Comment> findAll() {
        return (List<Comment>) commentRepository.findAll();
    }
}
