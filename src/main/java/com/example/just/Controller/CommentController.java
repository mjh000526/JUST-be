package com.example.just.Controller;

import com.example.just.Dao.Comment;
import com.example.just.Dto.*;
import com.example.just.Response.ResponseCommentDto;
import com.example.just.Response.ResponsePostCommentDto;
import com.example.just.Response.ResponsePostCommentDtoBefore;
import com.example.just.Service.CommentService;
import com.example.just.jwt.JwtProvider;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CommentController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private JwtProvider jwtProvider;

    private String errorMassage = "{\n"
            + "  \"comment_id\": \"\",\n"
            + "  \"comment_create_time\": 0\n"
            + "  \"comment_create_time\": \"\",\n"
            + "  \"comment_like\": 0\n"
            + "  \"comment_dislike\": 0\n"
            + "  \"blamed_count\": true\n"
            + "  \"child\": []\n"
            + "  \"message\": \"";

    @Operation(summary = "댓글 작성 api", description = "parentCommentId는 부모 댓글의 아이디\n"
            + "{\n"
            + "  \"comment_content\": \"안녕\",\n"
            + "  \"parent_comment_id\": 0\n"
            + "}" + "해당 예시는 첫 대글 작성시 예제임" + "\n"
            + "{\n"
            + "  \"comment_content\": \"안녕\",\n"
            + "  \"parent_comment_id\": 6\n"
            + "}" + "해당 예시는 6번 댓글의 대댓글 작성시 예제임")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"comment_id\": \"\",\n"
                    + "  \"comment_create_time\": 0\n"
                    + "  \"comment_create_time\": \"\",\n"
                    + "  \"comment_like\": 0\n"
                    + "  \"comment_dislike\": 0\n"
                    + "  \"blamed_count\": true\n"
                    + "  \"child\": []\n"
                    + "  \"message\": \"입력 완료\"\n"
                    + "}"),
            @ApiResponse(responseCode = "400", description = "{\n"
                    + "  \"comment_id\": \"\",\n"
                    + "  \"comment_create_time\": 0\n"
                    + "  \"comment_create_time\": \"\",\n"
                    + "  \"comment_like\": 0\n"
                    + "  \"comment_dislike\": 0\n"
                    + "  \"blamed_count\": true\n"
                    + "  \"child\": []\n"
                    + "  \"message\": \"해당 부모 댓글 없음, 대댓글에는 대댓글 작성 불가\"\n"
                    + "}")
    })
    @PostMapping("/post/{post_id}/comments")
    public ResponseEntity<ResponseCommentDto> createComment(@PathVariable Long post_id,
                                                            @RequestBody CommentDto comment_dto,
                                                            HttpServletRequest req) {
        Comment comment = null;
        Long member_id = null;
        try {
            String token = jwtProvider.getAccessToken(req);
            member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
            comment = commentService.createComment(post_id, member_id, comment_dto);

        } catch (NullPointerException e){
            return ResponseEntity.status(404).body(new ResponseCommentDto("해당 부모 댓글 없음"));
        } catch (RuntimeException e){
            return ResponseEntity.status(404).body(new ResponseCommentDto("대댓글에는 대댓글 작성 불가"));
        }
        return ResponseEntity.ok(new ResponseCommentDto(comment,member_id,"입력 완료"));
    }

    @ApiOperation(value = "댓글 조회 API")
    @GetMapping("v2/get/{post_id}/comments")
    public ResponseEntity<ResponsePostCommentDto> getCommentList(@PathVariable Long post_id, HttpServletRequest req) {
        return ResponseEntity.ok(commentService.getCommentList(post_id, req));
    }

    @ApiOperation(value = "댓글 조회 API")
    @GetMapping("v1/get/{post_id}/comments")
    public ResponseEntity<ResponsePostCommentDtoBefore> getCommentListBefore(@PathVariable Long post_id,
                                                                             HttpServletRequest req) {
        return ResponseEntity.ok(commentService.getCommentListBefore(post_id, req));
    }

    @Operation(summary = "댓글 삭제 api", description = "대댓글까지 다 삭제되니 유의해야 함")
    @DeleteMapping("/delete/comment/{post_id}/{comment_id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long post_id, @PathVariable Long comment_id) {
        return commentService.deleteComment(post_id, comment_id);
    }

    @ApiOperation(value = "댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",description = "댓글 내용"),
            @ApiResponse(responseCode = "400",description = "댓글이 존재하지 않습니다.\n게시물이 존재하지 않습니다.")
    })
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
    public ResponseEntity getMyComment(HttpServletRequest request) {
        Long member_id = 0L;
        if (request.getHeader("Authorization") != null) {
            String token = jwtProvider.getAccessToken(request);
            member_id = Long.valueOf(jwtProvider.getIdFromToken(token)); //토큰
        }
        if (member_id == 0L) {
            return new ResponseEntity<>("로그인 이후 이용해야 합니다.", HttpStatus.BAD_REQUEST);
        }
        return commentService.getMyComment(member_id);
    }

    private String errorMassage(String error){
        return "{\n"
                + "  \"comment_id\": \"\",\n"
                + "  \"comment_create_time\": 0\n"
                + "  \"comment_create_time\": \"\",\n"
                + "  \"comment_like\": 0\n"
                + "  \"comment_dislike\": 0\n"
                + "  \"blamed_count\": true\n"
                + "  \"child\": []\n"
                + "  \"message\": \"성공\"\n"
                + "}";
    }
}
