package search.service.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import search.model.Comment;
import search.model.Facility;
import search.repository.CommentRepository;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by Trayan_Muchev on 7/22/2016.
 */
@Service("commentService")
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final boolean NOT_DELETED_FIELDAS = FALSE;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private FacilityService facilityService;

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> findByFacilityId(Integer facilityId) {
        return commentRepository.findByFacilityIdAndIsDeleted(facilityId, NOT_DELETED_FIELDAS);
    }

    @Override
    public List<Comment> findByUserId(Integer userId) {
        return commentRepository.findByUserIdAndIsDeleted(userId, NOT_DELETED_FIELDAS);
    }

//    @Override
//    public void deleteComment(Integer id) {
//        commentRepository.delete(id);
//    }

    @Override
    public Facility deleteComment(Integer id) {
        Comment comment = commentRepository.findOne(id);
        comment.setDeleted(TRUE);
        commentRepository.save(comment);
//        return commentRepository.findByFacilityId(comment.getFacility().getId(), NOT_DELETED_FIELDAS);
        return facilityService.findById(comment.getFacility().getId());
    }

    @Override
    public Comment update(Comment comment) {
        Comment entity = commentRepository.findOne(comment.getId());

        entity.setText(comment.getText());
        return commentRepository.save(entity);
    }

    @Override
    public Comment findById(Integer commentId) {
        return commentRepository.findOne(commentId);
    }
}
