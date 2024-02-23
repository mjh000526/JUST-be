package com.example.just.Controller;

import com.example.just.Service.BlameService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = {"blame controller"},description = "신고 관련 api")
@RequestMapping("/api")
public class BlameController {
    @Autowired
    private BlameService blameService;

    @ApiOperation(value = "회원 신고 API" )
    @PostMapping(value = "/blame/{type_index}/member/{target_member_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"blame_id\": \"0\",\n"
                    + "  \"target_index\": 0\n"
                    + "  \"blame_date_time\": \"2024-02-23T08:17:43.603+00:00\",\n"
                    + "  \"blame_member_id\": 0\n"
                    + "  \"target_member_id\": 0\n"
                    + "  \"message\": \"회원 신고완료\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"이미 신고한 회원입니다. || 신고할 회원이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity writeMemberBlame(HttpServletRequest request, @PathVariable Long type_index, @PathVariable Long target_member_id){
        return blameService.writeMemberBlame(request,target_member_id,type_index);
    }
    @ApiOperation(value = "게시글 신고 API" )
    @PostMapping(value = "/blame/{type_index}/post/{target_post_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"blame_id\": \"0\",\n"
                    + "  \"target_index\": 0\n"
                    + "  \"blame_date_time\": \"2024-02-23T08:17:43.603+00:00\",\n"
                    + "  \"blame_member_id\": 0\n"
                    + "  \"target_post_id\": 0\n"
                    + "  \"message\": \"게시글 신고완료\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"이미 신고한 게시글입니다. || 신고할 게시글이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity writePostBlame(HttpServletRequest request, @PathVariable Long type_index,@PathVariable Long target_post_id){
        return blameService.writePostBlame(request,target_post_id,type_index);
    }

    @ApiOperation(value = "댓글 신고 API")
    @PostMapping(value = "/blame/{type_index}/comment/{target_comment_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"blame_id\": \"0\",\n"
                    + "  \"target_index\": 0\n"
                    + "  \"blame_date_time\": \"2024-02-23T08:17:43.603+00:00\",\n"
                    + "  \"blame_member_id\": 0\n"
                    + "  \"target_comment_id\": 0\n"
                    + "  \"message\": \"댓글 신고완료\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"이미 신고한 댓글입니다. || 신고할 댓글이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity writeCommentBlame(HttpServletRequest request, @PathVariable Long type_index, @PathVariable Long target_comment_id){
        return blameService.writeCommentBlame(request,target_comment_id,type_index);
    }

    @ApiOperation(value = "회원 신고 취소 API" )
    @DeleteMapping(value = "/blame/member/{target_member_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"message\": \"회원 신고취소\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"신고하지 않은 회원입니다. || 회원이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity deleteMemberBlame(HttpServletRequest request, @PathVariable Long target_member_id){
        return blameService.deleteMemberBlame(request,target_member_id);
    }

    @ApiOperation(value = "게시글 신고 취소 API" )
    @DeleteMapping(value = "/blame/post/{target_post_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"message\": \"게시글 신고취소\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"신고하지 않은 게시글입니다. || 게시글이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity deletePostBlame(HttpServletRequest request, @PathVariable Long target_post_id){
        return blameService.deletePostBlame(request,target_post_id);
    }

    @ApiOperation(value = "댓글 신고 취소 API" )
    @DeleteMapping(value = "/blame/comment/{target_comment_id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "{\n"
                    + "  \"message\": \"댓글 신고취소\"\n"
                    + "}"),
            @ApiResponse(responseCode = "404", description = "{\n"
                    + "  \"message\": \"신고하지 않은 댓글입니다. || 댓글이 존재하지 않습니다.\"\n"
                    + "}"),
    })
    public ResponseEntity deleteCommentBlame(HttpServletRequest request, @PathVariable Long target_comment_id){
        return blameService.deleteCommentBlame(request,target_comment_id);
    }

    @ApiOperation(value = "신고받은횟수 상위 멤버 조회")
    @GetMapping(value = "/blamed/member")
    public ResponseEntity viewBlamedMember(){
        return blameService.getBlamedList("member");
    }

    @ApiOperation(value = "신고받은횟수 상위 게시글 조회")
    @GetMapping(value = "/blamed/post")
    public ResponseEntity viewBlamedPost(){
        return blameService.getBlamedList("post");
    }

    @ApiOperation(value = "신고받은횟수 상위 댓글 조회")
    @GetMapping(value = "/blamed/comment")
    public ResponseEntity viewBlamedComment(){
        return blameService.getBlamedList("comment");
    }

    @ApiOperation(value = "신고한 횟수 상위 멤버 조회")
    @GetMapping(value = "/blame/member")
    public ResponseEntity viewBlameMember(){
        return blameService.getBlameList();
    }
}