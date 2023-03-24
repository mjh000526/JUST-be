package com.example.just.Controller;

import com.example.just.Service.BlameService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class BlameController {
    @Autowired
    private BlameService blameService;

    @ApiOperation(value = "게시글 신고 API")
    @PostMapping(value = "/blame/post/{target_post_id}")
    public ResponseEntity writepostBlame(HttpServletRequest request, @PathVariable Long target_post_id){
        return blameService.writeBlame(request,target_post_id,"post");
    }

    @ApiOperation(value = "댓글 신고 API")
    @PostMapping(value = "/blame/comment/{target_comment_id}")
    public ResponseEntity writeCommentBlame(HttpServletRequest request, @PathVariable Long target_comment_id){
        return blameService.writeBlame(request,target_comment_id,"comment");
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