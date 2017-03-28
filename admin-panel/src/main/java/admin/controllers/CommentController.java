package admin.controllers;

import admin.model.Comment;
import admin.model.DeleteItems;
import admin.service.persistance.CommentService;
import common.jsonwebtoken.AuthTokenDetailsDTO;
import common.jsonwebtoken.JsonWebTokenUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Trayan_Muchev on 12/6/2016.
 */
@PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
@RestController
@RequestMapping("/comments")
public class CommentController {

    private AuthTokenDetailsDTO detailsDTO;

    @Autowired
    private JsonWebTokenUtility tokenUtility;

    @Autowired
    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<List<Comment>> allFacilities(@RequestHeader("Authorization") String token) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(this.commentService.findAll());
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Comment> updateComment(@RequestHeader("Authorization") String token, @PathVariable Integer id, @RequestBody Comment comment) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.update(id, comment));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    ResponseEntity<Comment> getCommentById(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {
        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            return ResponseEntity.status(HttpStatus.OK).body(commentService.findById(id));
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    void deleteUser(@RequestHeader("Authorization") String token, @PathVariable("id") int id) {

        detailsDTO = tokenUtility.parseAndValidate(token);
        if (detailsDTO != null) {
            this.commentService.deleteComment(id);
            ResponseEntity.status(HttpStatus.OK);
        } else {
            ResponseEntity.status(HttpStatus.FORBIDDEN);
        }
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public List<Comment> deleteList(@RequestBody DeleteItems deleteComments) {
        return commentService.deleteList(deleteComments.getItemsId());
    }
}
