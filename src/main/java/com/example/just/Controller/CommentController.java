package com.example.just.Controller;

import com.example.just.Dao.Comment;
import com.example.just.Dto.CommentDto;
import com.example.just.Service.CommentService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @ApiOperation(value = "댓글 작성 API")
    @PostMapping("/post/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                 @RequestBody CommentDto commentDto,
                                                 @RequestParam Long member_id) {
        Comment comment = commentService.createComment(postId, member_id, commentDto);
        return ResponseEntity.ok(comment);
    }

    @ApiOperation(value = "댓글 조회 API")
    @GetMapping("/get/{postId}/comments")
    public ResponseEntity<List<Comment>> getCommentList(@PathVariable Long postId) {
        List<Comment> commentList = commentService.getCommentList(postId);
        return ResponseEntity.ok(commentList);
    }

}
