package com.example.just.Controller;

import com.example.just.Dao.Comment;
import com.example.just.Dto.*;
import com.example.just.Service.CommentService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "댓글 작성 api", description = "parentCommentId는 부모 댓글의 아이디\n"
            + "대댓글일이 아닐때는 0을 넣으면 댓글이 쓰이고 대댓글일시 1 또 그에대한 대댓글일시 2입력")
    @PostMapping("/post/{post_id}/comments")
    public ResponseEntity<Comment> createComment(@PathVariable Long post_id,
                                                 @RequestBody CommentDto comment_dto,
                                                 HttpServletRequest req) {
        String token = jwtProvider.getAccessToken(req);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        Comment comment = commentService.createComment(post_id, member_id, comment_dto);
        return ResponseEntity.ok(comment);
    }

    @ApiOperation(value = "댓글 조회 API")
    @GetMapping("/get/{post_id}/comments")
    public ResponseEntity<ResponsePostCommentDto> getCommentList(@PathVariable Long post_id, HttpServletRequest req) {
        return ResponseEntity.ok(commentService.getCommentList(post_id, req));
    }

    @Operation(summary = "댓글 삭제 api", description = "대댓글까지 다 삭제되니 유의해야 함")
    @DeleteMapping("/delete/comment/{post_id}/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long post_id, @PathVariable Long comment_id) {
        return commentService.deleteComment(post_id, comment_id);
    }

    @ApiOperation(value = "댓글 수정")
    @PutMapping("/put/comment/{post_id}/{comment_id}")
    public ResponseEntity<String> putComment(@PathVariable Long post_id, @PathVariable Long comment_id,
                                             @RequestBody PutCommentDto commentDto,
                                             HttpServletRequest req) {
        return commentService.putComment(post_id, comment_id, commentDto);
    }

    @ApiOperation(value = "댓글 신고")
    @PostMapping("/post/comment/{post_id}/{comment_id}")
    public ResponseEntity<String> blameComment(@PathVariable Long post_id, @PathVariable Long comment_id) {
        return commentService.blameComment(post_id, comment_id);
    }

    @ApiOperation(value = "댓글 신고 횟수 조회")
    @GetMapping("/get/comment/blame/{post_id}/{comment_id}")
    public int blameGetComment(@PathVariable Long post_id, @PathVariable Long comment_id) {
        return commentService.blameGetComment(post_id, comment_id);
    }

    @ApiOperation(value = "댓글 좋아요")
    @PostMapping("/post/like/comment/{postId}/{commentId}")
    public void likeComment(@PathVariable Long postId, @PathVariable Long commentId,
                            HttpServletRequest req) {
        String token = jwtProvider.getAccessToken(req);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));
        commentService.likeComment(postId, commentId, member_id);
    }

    @ApiOperation(value = "자신의 댓글 조회")
    @GetMapping("/get/member/comment")
    public List<ResponseGetMemberCommentDto> getMyComment(HttpServletRequest request) {
        String token = jwtProvider.getAccessToken(request);
        Long member_id = Long.valueOf(jwtProvider.getIdFromToken(token));
        return commentService.getMyComment(member_id);
    }
}
