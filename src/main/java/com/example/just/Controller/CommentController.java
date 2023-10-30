package com.example.just.Controller;

import com.example.just.Dao.Comment;
import com.example.just.Dto.CommentDto;
import com.example.just.Service.CommentService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtProvider jwtProvider;

    @ApiOperation(value = "댓글 작성 API")
    @PostMapping("/post/{postId}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long postId,
                                                 @RequestBody CommentDto commentDto,
                                                 HttpServletRequest req) {

        String token = jwtProvider.getAccessToken(req);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
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
