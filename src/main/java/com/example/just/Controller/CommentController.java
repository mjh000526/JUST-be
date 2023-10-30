package com.example.just.Controller;

import com.example.just.Dao.Comment;
import com.example.just.Dto.CommentDto;
import com.example.just.Dto.PutCommentDto;
import com.example.just.Service.CommentService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import javax.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
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

    @Operation(summary = "댓글 작성 api", description = "parentCommentId는 부모 댓글의 아이디\n"
            + "대댓글일이 아닐때는 0을 넣으면 댓글이 쓰이고 대댓글일시 1 또 그에대한 대댓글일시 2입력")
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

    @Operation(summary = "댓글 삭제 api", description = "대댓글까지 다 삭제되니 유의해야 함")
    @DeleteMapping("/delete/comment/{postId}/{commentId}")
    public ResponseEntity<String> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return commentService.deleteComment(postId, commentId);
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/put/comment/{postId}/{commentId}")
    public ResponseEntity<String> putComment(@PathVariable Long postId, @PathVariable Long commentId,
                                             @RequestBody PutCommentDto commentDto,
                                             HttpServletRequest req) {
        String token = jwtProvider.getAccessToken(req);
        return commentService.putComment(postId, commentId, commentDto);
    }

    @ApiOperation(value = "댓글 신고")
    @PostMapping("/post/comment/{postId}/{commentId}")
    public ResponseEntity<String> blameComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return commentService.blameComment(postId, commentId);
    }

    @ApiOperation(value = "댓글 신고 횟수 조회")
    @GetMapping("/get/comment/blame/{postId}/{commentId}")
    public int blameGetComment(@PathVariable Long postId, @PathVariable Long commentId) {
        return commentService.blameGetComment(postId, commentId);
    }
}
